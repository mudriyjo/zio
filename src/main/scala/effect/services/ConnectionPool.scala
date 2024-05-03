package effect.services

import zio._
import effect.services.{Connection, ConnectionPool}

class ConnectionPool(nConnection: Int) {
    def getConnection: Task[Connection] =
        ZIO.succeed(println("Getting connection...")) *> ZIO.succeed(new Connection())
}

object ConnectionPool {
    def create(nConnection: Int): ConnectionPool =
        new ConnectionPool(nConnection)
    
    def live(nConnection: Int): ZLayer[Any, Nothing, ConnectionPool] =
         ZLayer.succeed(create(nConnection))
}
