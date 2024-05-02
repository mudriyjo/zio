package Effect

import zio._
import msg
import scala.util.Try

object ErrorHandling extends ZIOAppDefault {

    val aFailedValue: IO[String, Nothing] = ZIO.fail("Something went wrong")
    val aThrowableValue: Task[Int] = ZIO.fail(new Throwable("Something went wrong"))
    val throwableToDescription: IO[String, Int] = aThrowableValue.mapError(e => e.getMessage())

    // attempt: run an effect that may throw an exception
    val badZIO: UIO[Int] = ZIO.succeed {
        println("Bad zio succeed example...")
        val str: String = null
        str.length()
    }

    // use attempt if you are ever unsure whether your code may throw
    val betterZIO: Task[Int] = ZIO.attempt {
        println("Better zio succeed example...")
        val str: String = null
        str.length()
    }

    // effectfuly catch error
    val catchErrors = aThrowableValue.catchAll(e => ZIO.succeed(s"error catched, msg: ${e.getMessage()}"))
    val catchSomeErrors = aThrowableValue.catchSome {
        case e: Throwable => ZIO.succeed(s"catch throwable error, msg: ${e.getMessage()}")
        case _ => ZIO.succeed(s"catch other type of error")
    }

    // Ignoring error
    val ignoerError = aThrowableValue.orElse(ZIO.succeed(42))
    
    // Handling error and value simultaniously
    val handleBoth = aThrowableValue.fold(
        e => s"catch error, msg: ${e.getMessage()}",
        value => value + 1
    )

    val handleZIO = aThrowableValue.foldZIO(
        e => ZIO.fail(s"catch error, msg: ${e.getMessage()}"),
        value => ZIO.succeed(value + 1)
    )

    // Convert option to ZIO
    val optionZIO: ZIO[Any, Option[Nothing], Int] = ZIO.fromOption(Some(42))

    // Convert either to ZIO
    val either: Either[String, Int] = Right(42)
    val eitherZIO: ZIO[Any, String, Int] = ZIO.fromEither(either)

    val eitherZIO_v2: ZIO[Any, Nothing, Either[String,Int]] = eitherZIO.either

    val eitherZIO_v3: ZIO[Any, String, Int] = eitherZIO_v2.absolve

    // Convert try to ZIO
    val tryZIO: ZIO[Any, Throwable, Int] = ZIO.fromTry(Try({
        // effect that may cause exception
        println("preparing value....")
        42
    }))

    /* 
    Exercise: implement a version of fromTry, fromOption, fromEither, either, absolve
    using fold and foldZIO
     */
    override def run = ???
}
