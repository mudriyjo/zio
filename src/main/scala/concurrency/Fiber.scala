package concurrency

import zio._

object Fiber extends ZIOAppDefault {

  extension [R,E,A](zio: ZIO[R,E,A])
    def debuggingTask: ZIO[R,E,A] =
      zio
        .tap(value => ZIO.succeed(println(s"${Thread.currentThread()}[Success]: ${value}")))
        .tapErrorCause(err => ZIO.succeed(println(s"${Thread.currentThread()}[Failed]: ${err}")))

  /*
  + 1. Fiber example
  + 2. Extension method to ZIO for debugging (tap)
  3. One thread execution
  4. Multiply threads execution
  5. Fiber Fork/Join
  6. Fiber await
  7. Fiber poll
  6. Fiber Zip
  7. Fiber orElse
  8. Fiber first ready
   */
  val numComputation = ZIO.succeed(42)
  val textComputation = ZIO.succeed("Some text...")

  val oneThreadExecution = for {
    num <- numComputation.debuggingTask
    text <- textComputation.debuggingTask
  } yield (num, text)

  override def run = oneThreadExecution
}
