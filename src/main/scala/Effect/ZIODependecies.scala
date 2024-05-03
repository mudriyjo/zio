package Effect

import zio._

object ZIODependecies extends ZIOAppDefault{
  
    /* 
    + 1. Implement User case class
    + 2. Implement UserSubscriptionServices
       + Use special call function to produce effect
    3. Implement companion object factories
    4. Implement live functionality for each object
        Show 2 type of composition in live horizontal/vertical
    5. Prepare programm
    6. Provide dependencies
    7. Show ZLayer.Debug funcationality for dependency graph
    8. Show Standart ZLayer services, such as: Clock, Random, System, Console
     */

    class UserSubscriptionServices(database: UserDatabase, email: UserEmail) {
        def subscribe(user: User): Task[Unit] = for {
            _ <- email.sendMail(user)
            _ <- database.insert(user)
        } yield ()
    }

    object  UserSubscriptionServices {
        def create(database: UserDatabase, email: UserEmail): ZLayer[UserDatabase & UserEmail, Nothing, UserSubscriptionServices] = 
            ZLayer.succeed(new UserSubscriptionServices(database, email))
        
        val live: ZLayer[UserDatabase & UserEmail, Nothing, UserSubscriptionServices] =
            ZLayer.fromFunction[UserDatabase & UserEmail](x => create(x._1, x._2))
    }

    def run= ???
}
