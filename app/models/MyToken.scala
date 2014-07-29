package models

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.joda.time._
import securesocial.core.providers.Token

case class MyToken (

		val uuid: String,
		val email: String, 
		val creationTime: Long, 
		val expirationTime: Long, 
		val isSignUp: Boolean

		) extends KeyedEntity[String]  {

	def id = uuid

}

object TokenDB extends Schema {
	val Tokens = table[MyToken]("tokens")
}

object MyToken {

	import TokenDB._

	def create(token: MyToken): MyToken = inTransaction {
		Tokens.insert(token)
	}

	def read(uuid : String): Option[MyToken] = inTransaction {
		Tokens.lookup(uuid)		
	}

	def update(token: MyToken) {
		inTransaction { 
			Tokens.update(token) 
		}
	}

	def delete(uuid: String) {
		inTransaction {
			Tokens.delete(uuid)
		}
	}
	
	def deleteAll(){
	  inTransaction {	    
	    Tokens.deleteWhere(t => 1 === 1)
	  }
	}
	
	def deleteExpired(){
	  inTransaction{
	    Tokens.deleteWhere(t => t.expirationTime lt new DateTime().getMillis())
	  }
	}
	
	object Converters {
	  
		implicit def tokenToMyToken(token: Token): MyToken = {
				MyToken(token.uuid, token.email, token.creationTime.getMillis(),
      															token.expirationTime.getMillis(), token.isSignUp)
		} 
  
		implicit def myTokenToToken(myToken : Option[MyToken]) : Option[Token] = {
				myToken map (myToken => Token(myToken.uuid, myToken.email, new DateTime(myToken.creationTime),
      		new DateTime(myToken.expirationTime), myToken.isSignUp))
		}
  
	}
	
}

