package Effect.Services

import effect.services.entity.User
import zio._

class UserDatabase(connectionPool: ConnectionPool) {
    def insert(user: User): Task[Unit] = for {
        con <- connectionPool.getConnection
        // Just example, use prepared query
        _ <- con.makeQuery(s"INSERT INTO user (name, email) VALUES (${user.name}, ${user.email})")
    } yield ()
}
