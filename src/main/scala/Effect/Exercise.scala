import java.time.LocalDateTime
object Exercise {
  
    case class MyIO[A](performUnsafe: () => A) {
        def map[B](f: A => B): MyIO[B] =
            MyIO[B](() => f(performUnsafe()))
        
        def flatMap[B](f: A => MyIO[B]): MyIO[B] =
            MyIO[B](() => f(performUnsafe()).performUnsafe())
    }

    val computation = MyIO[Int](() => {
        println("perform computation....")
        42
    })
    //1
    def getTime: MyIO[Long] = MyIO(() => { System.nanoTime() })
    //2
    def measure[A](computation: MyIO[A]): MyIO[(Long, A)] = for {
        start <- getTime
        value <- computation
        end <- getTime
    } yield (end - start, value)
    
    //3
    def readFromConsole: MyIO[String] =
        MyIO(() => {
            scala.io.StdIn.readLine()
        })
    //4
    def writeToConsole(text: String): MyIO[Unit] =
        MyIO(() => {
            println(text)
        })
    /* 
    Exercise create some IO which
    1. measure the current time
    2. measure the duration of computation
        - use exercise 1
        - use map/flatMap combination of MyIO
    3. Read something from the console
    4. Write something to the console (e.g "what's your name?") then read,
        then print a welcome message
     */

    def program = for {
        _ <- writeToConsole("what's is your name?")
        name <- readFromConsole
        _ <- writeToConsole(s"Hello, ${name}")
    } yield ()

    @main def main(): Unit = {
        val longComputation = MyIO(() => {Thread.sleep(1000); 42})
        // val res = computation.performUnsafe()
        println(s"computation is: ${measure(longComputation).performUnsafe()}")
        program.performUnsafe()
    }
}
