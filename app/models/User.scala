package models

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode.__thisDsl
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.Schema

import UserDB.Users

case class User (

		application_user_id: String,
		handle_name: String

		) extends KeyedEntity[String]{

	def id = application_user_id

}

object UserDB extends Schema {
	val Users = table[User]("users")
}

object User {
	
	import UserDB._ 
  
	def create(user: User): User = inTransaction {
		Users.insert(user)
	}

	def read(id : String): Option[User] = inTransaction {
		Users.lookup(id)		
	}

	def update(user: User) {
		inTransaction { 
			Users.update(user) 
		}
	}

	def delete(user: User) {
		inTransaction {
			Users.delete(user.application_user_id)
		}
	}
}