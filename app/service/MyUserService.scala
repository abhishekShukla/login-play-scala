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
import UserIdentity.Converters._
import MyToken.Converters._

class MyUserService(application: Application) extends UserServicePlugin(application) {

  val logger = Logger("application.controllers.UserService")

  def find(id: IdentityId): Option[Identity] = {
  	    
    if ( logger.isDebugEnabled ) {
      logger.debug("finding an identity")
    }
	     
    UserIdentity.read((id.userId, id.providerId)) 
    
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("finding an user using email and provider")
    }
    
    UserIdentity.findUserIdentityByEmail(email, providerId)
     
  }

  def save(user: Identity): Identity = {
    transaction {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("saving an identity")
    }
    
    val maybeUser = UserIdentity.read((user.identityId.userId, user.identityId.providerId))
      
    maybeUser.map(existingUser => UserIdentity.update((convertIdentityToUserIdentity(existingUser.application_user_id)(user))))
    				 .getOrElse(UserIdentity.create(newUser(user))) 
      user
    }
  }

  def save(token: Token) {
       
    if ( logger.isDebugEnabled ) {
      logger.debug("saving a token")
    }
    	
    MyToken.create(token)
  }

  def findToken(uuid: String): Option[Token] = {
    
    if ( logger.isDebugEnabled ) {
      logger.debug("Finding a token")
    }
    
    MyToken.read(uuid)
    
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
 
	

}