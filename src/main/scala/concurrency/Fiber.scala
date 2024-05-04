package concurrency

import zio.*

import java.io.FileWriter

object Fiber extends ZIOAppDefault {

  extension [R,E,A](zio: ZIO[R,E,A])
    def debuggingTask: ZIO[R,E,A] =
      zio
        .tap(value => ZIO.succeed(println(s"${Thread.currentThread()}[Success]: ${value}")))
        .tapErrorCause(err => ZIO.succeed(println(s"${Thread.currentThread()}[Failed]: ${err}")))

  /*
  + 1. Fiber example
  + 2. Extension method to ZIO for debugging (tap)
  + 3. One thread execution
  + 4. Multiply threads execution
  + 5. Fiber Fork/Join
  + 6. Fiber await
  + 7. Fiber poll
  + 6. Fiber Zip
  + 7. Fiber orElse
  + 8. Fiber first ready
   */
  val numComputation = ZIO.succeed({
    Thread.sleep(100)
    42
  })
  val textComputation = ZIO.succeed({
    Thread.sleep(200)
    "Some text..."
  })

  val oneThreadExecution = for {
    num <- numComputation.debuggingTask
    text <- textComputation.debuggingTask
  } yield (num, text)

  val multiplyThreadExecution = for {
    numFib <- numComputation.debuggingTask.fork
    textFib <- textComputation.debuggingTask.fork
  } yield ()

  val multiplyThreadExecutionWithResult = for {
    numFib <- numComputation.debuggingTask.fork
    textFib <- textComputation.debuggingTask.fork
    num <- numFib.join
    text <- textFib.join
    // res <- numFib.zip(textFib).join
  } yield (num, text)

  val multiplyThreadExecutionAwait = for {
    numFib <- numComputation.debuggingTask.fork
    textFib <- textComputation.debuggingTask.fork
    res <- numFib.zip(textFib).await
  } yield res match
    case Exit.Success(value) => (value._1, value._2)
    case Exit.Failure(cause) => s"exception: ${cause}"

  val multiplyThreadExecutionPolling = for {
    numFib <- numComputation.debuggingTask.fork
    textFib <- textComputation.debuggingTask.fork
    num <- numFib.poll
    text <- textFib.poll
    // res <- numFib.zip(textFib).join
  } yield (num, text)

  val defaultParameterExample = {
    val zio = ZIO.fail("some error happens").orElse(ZIO.succeed("defaultValue"))
    for {
      fib <- zio.debuggingTask.fork
      res <- fib.join
    } yield res
  }

  val fastestComputation = for {
    fastest <- numComputation.debuggingTask.race(textComputation.debuggingTask)
  } yield fastest

  /**
   * Exercises
   * 1. Zip 2 fibers without using zip combinator
   * 2. orElse 2 fiber without orElse combinator
   * 3. distribution a task between many fibers
   *  spawn n fibers, count the n of words if each file,
   *  then aggregate all the results in one big number
   */

  //1.
  def zipFiber[E,A,B](f1: Fiber[E,A], f2: Fiber[E,B]): ZIO[Any, Nothing, Fiber[E, (A,B)]] = ???

  //2.
  def orElse[E,A](f1: Fiber[E,A], f2: Fiber[E,A]): ZIO[Any, Nothing, Fiber[E, A]] = ???

  //3.
  def generateFile(path: String): Unit = {
    val numberOfWord = 2000
    val chars = 'a' to 'z'
    val random = java.util.Random()
    val fileWords = (0 to random.nextInt(numberOfWord)).map(_ => {
      (1 to 10).map(_ => chars(random.nextInt(26))).mkString
    }).mkString(" ")

    val file = new FileWriter(path)
    file.write(fileWords)
    file.close()
  }

  val prepareTestFiles = ZIO.succeed({
    (1 to 10).map(i => generateFile(s"src/main/resources/test_file_$i.txt"))
  })

  override def run = ???
}
