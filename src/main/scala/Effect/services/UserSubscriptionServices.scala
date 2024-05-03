package effect.Services

import effect.services.entity.User
import zio._

class UserSubscriptionServices(database: UserDatabase, email: UserEmail) {
    
    def subscribe(user: User): Task[Unit] = for {
            _ <- email.sendMail(user)
            _ <- database.insert(user)
        } yield ()
}

object  UserSubscriptionServices {
    def create(database: UserDatabase, email: UserEmail): UserSubscriptionServices = 
        new UserSubscriptionServices(database, email)
    
    val live: ZLayer[UserDatabase & UserEmail, Nothing, UserSubscriptionServices] =
        ZLayer.fromFunction(create)
}
