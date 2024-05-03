package effect.services
import zio._

class Connection() {
    def makeQuery(query: String): Task[Unit] = 
        ZIO.succeed(println(s"Performp query: ${query}"))
}