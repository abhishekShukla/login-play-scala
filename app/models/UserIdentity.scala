package models

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import org.squeryl.Schema
import org.squeryl.PersistenceStatus
import org.squeryl.Query


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
	
	def create(userIdentity: UserIdentity): UserIdentity = inTransaction {
		UserIdentities.insert(userIdentity)
	}

	def read(IdentityId : (String, String)): Option[UserIdentity] = inTransaction {
	  
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
	  
	  (from(UserIdentities) ( u => where(u.provider === providerId and 
			  							 u.email === email) select (u))).headOption
	  
	}
	
	def findAllUserIdentities(IdentityId : (String, String)): List[UserIdentity] = inTransaction{
	  
	  //TODO needs to looked into. This is a hack
	  val user = (from(UserIdentities) ( u => where(u.id === IdentityId) select (u))).headOption
	  
	  user match {
	    case Some(user) => (from(UserIdentities) ( u => where(u.application_user_id === user.application_user_id) select (u))).toList  
	    case None => None.toList
	  }
	  
	  
	}

}