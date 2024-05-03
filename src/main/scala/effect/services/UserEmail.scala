package effect.services

import effect.services.entity.User
import zio._
import effect.services.entity.User
import effect.services.UserEmail

class UserEmail() {
    def sendMail(user: User): Task[Unit] =
        ZIO.succeed(println(s"Send mail to user email: ${user.email}"))
}

object UserEmail {
    def create: UserEmail = new UserEmail()
    val live: ZLayer[Any, Nothing, UserEmail] = ZLayer.succeed(create)
}
