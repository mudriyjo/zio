package effect.Services

import effect.services.entity.User
import zio._

class UserEmail() {
    def sendMail(user: User): Task[Unit] =
        ZIO.succeed(println(s"Send mail to user email: ${user.email}"))
}

object UserEmail {
    def create: UserEmail = new UserEmail()
    val live: ZLayer[Nothing, Nothing, UserEmail] = ZLayer.succeed(create)
}
