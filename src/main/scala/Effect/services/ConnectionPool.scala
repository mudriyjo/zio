package effect.Services
import zio._

class ConnectionPool(nConnection: Int) {
    def getConnection: Task[Connection] =
        ZIO.succeed(println("Getting connection...")) *> ZIO.succeed(new Connection())
}

object ConnectionPool {
    def create(nConnection: Int): ConnectionPool =
        new ConnectionPool(nConnection)
    
    def live(nConnection: Int): ZLayer[Nothing, Nothing, ConnectionPool] =
         ZLayer.succeed(create(nConnection))
}
