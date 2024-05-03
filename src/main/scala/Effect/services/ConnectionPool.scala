package Effect.Services
import zio._

class ConnectionPool(nConnection: Int) {
    def getConnection: Task[Connection] =
        ZIO.succeed(println("Getting connection...")) *> ZIO.succeed(new Connection())
}
