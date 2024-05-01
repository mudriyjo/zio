object Exercise {
  
    class MyIO[A](val performUnsafe: () => A) {
        def map[B](f: A => B): MyIO[B] =
            MyIO[B](() => f(performUnsafe()))
        
        def flatMap[B](f: A => MyIO[B]): MyIO[B] =
            MyIO[B](() => f(performUnsafe()).performUnsafe())
    }

    val computation = MyIO[Int](() => {
        println("perform computation....")
        42
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

    @main def main(): Unit = {
        val res = computation.performUnsafe()
        println(s"computation is: $res")
    }
}
