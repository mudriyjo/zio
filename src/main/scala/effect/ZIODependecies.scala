package effect

import zio._
import effect.services._
import effect.services.entity._

object ZIODependecies extends ZIOAppDefault {
  
    /* 
    + 1. Implement User case class
    + 2. Implement UserSubscriptionServices
       + Use special call function to produce effect
    + 3. Implement companion object factories
    + 4. Implement live functionality for each object
        + Show 2 type of composition in live horizontal/vertical
    + 5. Prepare programm
    + 6. Provide dependencies
    7. Show ZLayer.Debug funcationality for dependency graph
    8. Show Standart ZLayer services, such as: Clock, Random, System, Console
     */

   

    val subscriptionServices: ZLayer[Any, Nothing, UserSubscriptionServices] = 
        (ConnectionPool.live(10) >>> UserDatabase.live ++ UserEmail.live)
            >>> UserSubscriptionServices.live

    val program: ZIO[UserSubscriptionServices, java.lang.Throwable, Unit] = for {
       subscribeService <- ZIO.service[UserSubscriptionServices]
       _ <- subscribeService.subscribe(User("Alex", "alex@gmail.com"))
       _ <- subscribeService.subscribe(User("John", "john@gmail.com"))
    } yield()
    
    def run = program.provide(subscriptionServices)
}
