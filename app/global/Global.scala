package global

import org.squeryl.adapters.MySQLAdapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
	
  override def onStart(app: Application) {
    Class.forName("com.mysql.jdbc.Driver")
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DB.getConnection()(app), new MySQLAdapter) )
  }
  
}