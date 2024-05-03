package effect

import zio.*
import effect.services.*
import effect.services.entity.*

import java.time.temporal.ChronoUnit

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
    + 7. Show ZLayer.Debug funcationality for dependency graph
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
    
    val program_v2 = for {
        time <- Clock.currentTime(ChronoUnit.MILLIS)
        _ <- Console.printLine(s"seconds are: ${time}")
        _ <- Console.printLine(s">>>>>>>>>>>>>>>>>>>>>")
        _ <- program
        _ <- Console.printLine(s">>>>>>>>>>>>>>>>>>>>>")
        time <- Clock.currentTime(ChronoUnit.MILLIS)
        _ <- Console.printLine(s"seconds after call are: ${time}")
        num <- Random.nextIntBetween(0, 100)
        _ <- Console.printLine(s"random number is: ${num}")
        envValue <- System.env("Test")
        _ <- Console.printLine(s"Test value is: ${envValue}")
    } yield()

    def run = program_v2.provide(
        // subscriptionServices
        UserSubscriptionServices.live,
        UserEmail.live,
        UserDatabase.live,
        ConnectionPool.live(10),
        // ZLayer.Debug.tree
        // ZLayer.Debug.mermaid
    )
}
