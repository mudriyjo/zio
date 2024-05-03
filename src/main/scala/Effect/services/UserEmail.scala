package Effect.Services

import effect.services.entity.User
import zio._

class UserEmail() {
    def sendMail(user: User): Task[Unit] =
        ZIO.succeed(println(s"Send mail to user email: ${user.email}"))
}
