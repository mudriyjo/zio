package Effect

import zio._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import java.io.IOException

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

    def fromTry[A](el: Try[A]): ZIO[Any, Throwable, A] = el match
        case Failure(exception) => ZIO.fail(exception)
        case Success(value) =>ZIO.succeed(value)

    def fromOption[A](el: Option[A]): ZIO[Any, Option[Nothing], A] = el match
        case None => ZIO.fail(None)
        case Some(value) => ZIO.succeed(value)
    
    def fromEither[A,E](el: Either[E, A]): ZIO[Any, E, A] = el match
        case Left(value) => ZIO.fail(value)
        case Right(value) => ZIO.succeed(value)
    
    def either[R, E,A](el: ZIO[R, E, A]): ZIO[R, Nothing, Either[E,A]] = 
        el.fold(e => Left(e), value => Right(value))
    
    def absolve[R,E,A](el: ZIO[R, Nothing, Either[E,A]]): ZIO[R, E, A] = 
        el.flatMap(value => value match {
            case Left(value) => ZIO.fail(value)
            case Right(value) => ZIO.succeed(value)
        })

    
    /* 
        Errors = failures present in the ZIO type signature ("checked" errors)
        Defects = failures that are unrecoverable, unforseen, NOT present in the ZIO type signature

        ZIO[R, E, A] can finish with Exit[E, A]
        - Success[A] containing a value
        - Cause[E]
            - Fail[E] containing error
            - Die(t: Throwable) which was unforseen
    */

    val divisedByZero: UIO[Int] = ZIO.succeed(1 / 0)
    val failedCausedExpected: ZIO[Any, String, Int] = ZIO.fail("Something happend....")
    val failedCauseExposed: ZIO[Any, Cause[String], Int] = failedCausedExpected.sandbox
    val failedCauseHidden: ZIO[Any, String, Int] = failedCauseExposed.unsandbox

    // fold with cause
    val foldWithCause = failedCausedExpected.foldCause(
        cause => s"error happend, ${cause.defects}",
        res => res
    )

    val foldWithCauseZIO = failedCausedExpected.foldCauseZIO(
        cause => ZIO.succeed("error happend, ${cause.defects}"),
        res => ZIO.succeed(res)
    )

    /* 
    Good practices
    1. At a low level, your errors should be treated
    2. At a high level, you should hide errors and assume they are unrecoverable
     */

    def callHttpEndpoint(url: String): ZIO[Any, IOException, String] =
        ZIO.fail(new IOException("Can't reach host"))

    val endpointCallWithDefect = callHttpEndpoint("http://g.com").orDie

    // refining the error channel
    def callHttpEndpointWideError(url: String): ZIO[Any, Exception, String] =
        ZIO.fail(new IOException("Can't reach host"))
    
    def callHttpEndpoint_v2(url: String): ZIO[Any, IOException, String] =
        callHttpEndpointWideError(url).refineOrDie{
            case e: IOException => e
            case e: Exception => new IOException(e.getMessage())
        }
    
    // reverse make error wider
    val unrefineCallHttp: ZIO[Any, Exception, String] = 
        callHttpEndpoint_v2("http://g.com").unrefine{
            case e => new Exception(e.getMessage())
        }
    
    // combine different type of errors
    // trait AppError
    case class IndexError(msg: String) // extends AppError
    case class DBError(msg: String) // extends AppError
    val callApi: ZIO[Any, IndexError, String] = ZIO.succeed("<html></html>")
    val makeQuery: ZIO[Any, DBError, Int] = ZIO.succeed(1)
    val combined = for {
        page <- callApi
        rowsAffected <- makeQuery
    } yield (page, rowsAffected)

    /* 
    Solution
    1. Design error model
    2. Using union type in Scala 3
    3. .mapError to some common error type
     */

              
    override def run = ZIO.none
}
