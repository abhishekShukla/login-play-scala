package models

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import org.squeryl.Schema
import org.squeryl.PersistenceStatus
import org.squeryl.Query
import securesocial.core._
import securesocial.core.providers.Token
import org.joda.time._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent.Akka
import play.libs.Akka._

case class UserIdentity (

		application_user_id: String,
		provider_user_id: String, 
		provider: String, 
		first_name: String, 
		last_name: String, 
		full_name: String, 
		email: String, 
		avatar_url: String, 
		auth_method: String, 
		password_crypt: String, 
		password: String, 
		oauth1_token: String, 
		oauth1_secret: String, 
		oauth2_token: String, 
		oauth2_expiry: Int 	

		) extends KeyedEntity[CompositeKey2[String, String]] {

		def id =  compositeKey(provider_user_id, provider)

}

object UserIdentityDB extends Schema {

	val UserIdentities = table[UserIdentity]("user_identities")

}

object UserIdentity {

	import UserIdentityDB._

	def create(identity: UserIdentity): UserIdentity = inTransaction { 	  
	  	UserIdentities.insert(identity)
	}

	def read(IdentityId : (String, String)) : Option[UserIdentity] = 
		inTransaction {
			(from(UserIdentities) ( u => where(u.id === IdentityId) select (u))).headOption
	}

	def update(userIdentity: UserIdentity) {
		inTransaction { 
			UserIdentities.update(userIdentity) 
		}
	}

	def delete(userIdentity: UserIdentity) {
		inTransaction {
			val key = (userIdentity.provider_user_id, userIdentity.provider)
					UserIdentities.deleteWhere(_.id === key)
		}
	}

	def findUserIdentityByEmail(email: String, providerId: String): Option[UserIdentity] = inTransaction {
	  
		(from(UserIdentities) ( u => where(u.provider === providerId and u.email === email) select (u))).headOption

	}
	
	object Converters {
	  
	  def convertIdentityToUserIdentity(application_user_id: String)(identity: Identity): UserIdentity = {

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

	def newUser(identity: Identity) : UserIdentity = convertIdentityToUserIdentity(System.currentTimeMillis().toString)(identity)
  
  implicit def userIdentityToIdentity(userIdentity: Option[UserIdentity]): Option[Identity] = {
		  
		userIdentity map {userIdentity =>
		  
				val identityId = IdentityId(userIdentity.provider_user_id, userIdentity.provider)
				val oauth1Info = Option(OAuth1Info(userIdentity.oauth1_secret, userIdentity.oauth1_token))
				val oauth2Info = Option(OAuth2Info(userIdentity.oauth2_token, None, Option(userIdentity.oauth2_expiry), None))
				val passwordInfo = Option(PasswordInfo(userIdentity.password_crypt, userIdentity.password, None))
				val authenticationMethod = AuthenticationMethod(userIdentity.auth_method)

				SocialUser(identityId, userIdentity.first_name, userIdentity.last_name, userIdentity.full_name,
						Option(userIdentity.email), Option(userIdentity.avatar_url), authenticationMethod,
						oauth1Info, oauth2Info, passwordInfo)
			}
			
		}
	}
	

}