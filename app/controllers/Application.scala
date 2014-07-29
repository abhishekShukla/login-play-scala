package controllers

import play.api._
import play.api.mvc._
import securesocial.core.{IdentityId, UserService, Identity, Authorization}
import play.api.{Logger, Play}
import service.MyUserService

object Application extends Controller with securesocial.core.SecureSocial {

	val logger = Logger("application.controllers.Application")
			def index = SecuredAction { implicit request =>
			logger.warn("logging from application")
			Ok(views.html.index(request.user))
	}

	// a sample action using the new authorization hook
	def onlyTwitter = SecuredAction(WithProvider("twitter")) { implicit request =>
		val logger = Logger("application.controllers.Application.onlyTwitter")
		logger.error("only twitter")
		//
		//    Note: If you had a User class and returned an instance of it from UserService, this
		//          is how you would convert Identity to your own class:
		//
		//    request.user match {
		//      case user: User => // do whatever you need with your user class
		//      case _ => // did not get a User instance, should not happen,log error/throw exception
		//    }
		Ok("You can see this because you logged in using Twitter")
	}
	
	// a sample action using the new authorization hook
	def onlyFacebook = SecuredAction(WithProvider("facebook")) { implicit request =>
		val logger = Logger("application.controllers.Application.onlyFacebook")
		logger.error("only Facebook")
		//
		//    Note: If you had a User class and returned an instance of it from UserService, this
		//          is how you would convert Identity to your own class:
		//
		//    request.user match {
		//      case user: User => // do whatever you need with your user class
		//      case _ => // did not get a User instance, should not happen,log error/throw exception
		//    }
		Ok("You can see this because you logged in using Facebook")
	}
	
	// a sample action using the new authorization hook
	def onlyGithub = SecuredAction(WithProvider("github")) { implicit request =>
		val logger = Logger("application.controllers.Application.onlyGithub")
		logger.error("only Github")
		//
		//    Note: If you had a User class and returned an instance of it from UserService, this
		//          is how you would convert Identity to your own class:
		//
		//    request.user match {
		//      case user: User => // do whatever you need with your user class
		//      case _ => // did not get a User instance, should not happen,log error/throw exception
		//    }
		Ok("You can see this because you logged in using Github")
	}

}

// An Authorization implementation that only authorizes uses that logged in using twitter
case class WithProvider(provider: String) extends Authorization {
	def isAuthorized(user: Identity) = {
		user.identityId.providerId == provider
	}

}