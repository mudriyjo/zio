package Effect

import zio._
import Effect.Services.UserSubscriptionServices
import Effect.Services.UserEmail

object ZIODependecies extends ZIOAppDefault{
  
    /* 
    + 1. Implement User case class
    + 2. Implement UserSubscriptionServices
       + Use special call function to produce effect
    + 3. Implement companion object factories
    + 4. Implement live functionality for each object
        Show 2 type of composition in live horizontal/vertical
    5. Prepare programm
    6. Provide dependencies
    7. Show ZLayer.Debug funcationality for dependency graph
    8. Show Standart ZLayer services, such as: Clock, Random, System, Console
     */

    import effect.services._
    val subscriptionServices = 
        UserDatabase.live ++ UserEmail.live

    def run= ???
}
