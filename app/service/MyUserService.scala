package service

import play.api.{ Logger, Application }
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId
import models._
import org.joda.time._
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import org.squeryl.Schema
import org.squeryl.PersistenceStatus
import org.squeryl.Query

class MyUserService(application: Application) extends UserServicePlugin(application) {

  val logger = Logger("application.controllers.UserService")


  def find(id: IdentityId): Option[Identity] = {
	 
    if ( logger.isDebugEnabled ) {
      logger.debug("finding an identity")
    }
	 
    val result = UserIdentity.read((id.userId, id.providerId))

    result match {
      case Some(result) => {
        Some(converUserIdentityToIdentity(result))
      }
      case _ => None
    }

  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
	  
    if ( logger.isDebugEnabled ) {
      logger.debug("finding an user using email and provider")
    }
    
    val result = UserIdentity.findUserIdentityByEmail(email, providerId)

    result match {
      case Some(result) => {
        Some(converUserIdentityToIdentity(result))
      }
      case _ => None
    }

  }

  def save(user: Identity): Identity = {
    transaction {
      
    if ( logger.isDebugEnabled ) {
      logger.debug("saving an identity")
    }
      val maybeUser = UserIdentity.read((user.identityId.userId, user.identityId.providerId))
      
      maybeUser match {
        case Some(existingUser) => {
          UserIdentity.update(convertIdentityToUserIdentity(existingUser.application_user_id, user))
        }
        case _ =>
          val newId = System.currentTimeMillis().toString
          UserIdentity.create(convertIdentityToUserIdentity(newId, user))
      }
      user
    }
  }

  def link(current: Identity, to: Identity) {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("Linking two identities")
    }
    val currentUser = UserIdentity.read((current.identityId.userId, current.identityId.providerId))
    var currentUserApplicationUserId = ""
    
    currentUser match {
      case Some(currentUser) => currentUserApplicationUserId = currentUser.application_user_id 
      case _ => None
    }
    
    val maybeToUser = UserIdentity.read((to.identityId.userId, to.identityId.providerId))
    
    
    maybeToUser match {
      case Some(existingUser) => {
        UserIdentity.update(convertIdentityToUserIdentity(currentUserApplicationUserId, to))
      }
      case _ =>
        UserIdentity.create(convertIdentityToUserIdentity(currentUserApplicationUserId, to))
    }

  }
  
  def getLinkedAccounts(identityId: IdentityId): List[Identity] = {
    
    val userIdentities = UserIdentity.findAllUserIdentities((identityId.userId, identityId.providerId))
    
    userIdentities.map(identity => converUserIdentityToIdentity(identity))
    
  }

  def save(token: Token) {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("saving a token")
    }
    
    val myToken = MyToken(token.uuid, token.email, token.creationTime.getMillis(),
      token.expirationTime.getMillis(), token.isSignUp)
    MyToken.create(myToken)
  }

  def findToken(token: String): Option[Token] = {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("Finding a token")
    }
    
    val myToken = MyToken.read(token)
    
    myToken match {
      case Some(myToken) => {
        Some(Token(myToken.uuid, myToken.email, new DateTime(myToken.creationTime),
          new DateTime(myToken.expirationTime), myToken.isSignUp))
      }
      case _ => None
    }

  }

  def deleteToken(uuid: String) {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("deleting one token")
    }
    
    MyToken.delete(uuid)
  }

  def deleteTokens() {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("deleting all tokens")
    }
    
    MyToken.deleteAll
  }

  def deleteExpiredTokens() {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("deleting expired tokens")
    }
    
    MyToken.deleteExpired
  }

  def converUserIdentityToIdentity(userIdentity: UserIdentity): Identity = {

    val identityId = IdentityId(userIdentity.provider_user_id, userIdentity.provider)
    val oauth1Info = Option(OAuth1Info(userIdentity.oauth1_secret, userIdentity.oauth1_token))
    val oauth2Info = Option(OAuth2Info(userIdentity.oauth2_token, None, Option(userIdentity.oauth2_expiry), None))
    val passwordInfo = Option(PasswordInfo(userIdentity.password_crypt, userIdentity.password, None))
    val authenticationMethod = AuthenticationMethod(userIdentity.auth_method)

    SocialUser(identityId, userIdentity.first_name, userIdentity.last_name, userIdentity.full_name,
      Option(userIdentity.email), Option(userIdentity.avatar_url), authenticationMethod,
      oauth1Info, oauth2Info, passwordInfo)

  }

  def convertIdentityToUserIdentity(application_user_id: String, identity: Identity): UserIdentity = {

    val providerUserId = identity.identityId.userId
    val providerId = identity.identityId.providerId
    val firstName = identity.firstName
    val lastName = identity.lastName
    val fullName = identity.fullName
    val email = identity.email.getOrElse("")
    val avatarUrl = identity.avatarUrl.getOrElse("")
    val auth_method = identity.authMethod.method

    val oauth1Info: OAuth1Info = identity.oAuth1Info.getOrElse(OAuth1Info("", ""))
    val oauth1Secret = oauth1Info.secret
    val oauth1Token = oauth1Info.token

    val oauth2Info = identity.oAuth2Info.getOrElse(OAuth2Info("", None, None, None))
    val oauth2Token = oauth2Info.accessToken
    val oauth2ExpriesIn = oauth2Info.expiresIn.getOrElse(0)

    val passwordInfo = identity.passwordInfo.getOrElse(PasswordInfo("", "", None))
    val passwordCrypt = passwordInfo.hasher
    val password = passwordInfo.password

    UserIdentity(application_user_id, providerUserId, providerId, firstName,
      lastName, fullName, email, avatarUrl, auth_method,
      passwordCrypt, password, oauth1Secret, oauth1Token, oauth2Token, oauth2ExpriesIn)

  }
}