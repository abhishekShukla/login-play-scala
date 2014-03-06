import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.squeryl.PrimitiveTypeMode.inTransaction

import models.User
import models.UserDB
import play.api.test.FakeApplication
import play.api.test.Helpers.running
import org.scalatest.Assertions._


class UsersSpec extends FlatSpec with Matchers  {
	"A user" should "should not be retrieved" in {
		running(FakeApplication()){
			inTransaction {
				var getUser = User.read("Abhishek")
						getUser match {
						case Some(user) => user.id should equal ("Abhishek")
						case None => None
				}
			}
		}
	}	

	"A user" should "be creatable" in {
		running(FakeApplication()){
			inTransaction {
				var user = User.create(new User("Abhishek", "abhishukla"))
						user.application_user_id should not equal(0)
			}
		}
	}

	"A user" should "not be creatable" in {
		running(FakeApplication()){
			inTransaction {

				a [java.lang.RuntimeException] should be thrownBy 
				User.create(new User("Abhishek", "abhishukla")) 

			}
		}
	}

	"A user" should "should be retrieved" in {
		running(FakeApplication()){
			inTransaction {
				var getUser = User.read("Abhishek")
						getUser match {
						case Some(user) => user.id should equal ("Abhishek")
						case None => None
				}
			}
		}
	}

	"A user" should "be deleted" in {
		running(FakeApplication()){
			inTransaction {
				val user = new User("Abhishek", "abhisukla")
				var deleteUser = User.delete(user)
				println("Here!" + deleteUser)
			}
		}
	}
}