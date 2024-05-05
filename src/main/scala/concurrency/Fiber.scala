package concurrency

import zio.*

import java.io.FileWriter

object Fiber extends ZIOAppDefault {

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
  def zipFiber[E,A,B](f1: Fiber[E,A], f2: Fiber[E,B]): ZIO[Any, Nothing, Fiber[E, (A,B)]] =
    f1.mapFiber(f1res => {
      f2.map(f2res => (f1res, f2res))
    })

  val zipFiberTest = for {
    f1 <- numComputation.fork
    f2 <- textComputation.fork
    fib <- zipFiber(f1,f2)
    res <- fib.join
    _ <- Console.printLine(res)
  } yield res

  //2.
  def orElse[E,A](f1: Fiber[E,A], f2: Fiber[E,A]): ZIO[Any, Nothing, Fiber[E, A]] =
    f1.await.map{
      case Exit.Failure(cause) => f2
      case Exit.Success(value) => f1
    }

  val testOrElse = {
    for {
      fib1 <- ZIO.fail("some error happend...").fork
      fib2 <- ZIO.succeed("Yehuu, it's working...").fork
      fibRes <- orElse(fib1, fib2)
      res <- fibRes.join
      _ <- Console.printLine(res)
    } yield ()
  }

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

  def calculateNumberOfWordInFile(path: String): Int = {
    val file = scala.io.Source.fromFile(path)
    val content = file.getLines()
    Thread.sleep(1000)
    content.mkString.split(" ").length
  }

  def splitTask(xs: List[String]): ZIO[Any, Throwable, Int] =
    xs.map(path => {
      ZIO.succeed(calculateNumberOfWordInFile(path))
    }).map(_.debuggingTask.fork)
      .map(x => x.flatMap(_.join))
      .reduce((zio1, zio2) => {
        for {
          x <- zio1
          y <- zio2
        } yield x + y
      })
//    val res = xs.tail.foldLeft(first)((acc, el) =>
//      acc.zipWithPar(ZIO.succeed(calculateNumberOfWordInFile(el)).fork)((a,b) => {
//        a.zipWith(b)((x,y) => x + y)
//    }))
//    ZIO.mergeAllPar(res)(ZIO.succeed(0))((a,b) => {
//      for {
//        acc <- a
//        value <- b.join
//      } yield acc + value
//    })
//    val r = res.tail.foldLeft(res.head)((acc, el) => for {
//      accFib <- acc
//      elFib <- el
//      elVal <- ZIO.succeed(accFib.zipWith(elFib)((a,b) => a + b))
//    } yield elVal)


  val prepareTestFiles = ZIO.succeed({
    (1 to 10).map(i => generateFile(s"src/main/resources/test_file_$i.txt"))
  })

  val testSplitTaskParallel = {
    val files = (1 to 10).map(i => s"src/main/resources/test_file_$i.txt").toList
    splitTask(files).map(println)
  }

  val testSplitTaskSeq = {
    val files = (1 to 10).map(i => s"src/main/resources/test_file_$i.txt").toList
    val res = files.foldLeft(0)((acc, f) => {
      println(s"file seq: ${calculateNumberOfWordInFile(f)}")
      acc + calculateNumberOfWordInFile(f)
    })
  }

  override def run = testSplitTaskParallel //*> ZIO.succeed(testSplitTaskSeq)
}
