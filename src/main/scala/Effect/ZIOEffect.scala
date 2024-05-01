package Effect

import zio._
import zio.Cause.Empty

object ZIOEffect {

    // 1. - sequence two ZIOs and take value of the last one
    def sequenceTakeLast[R,E,A,B](zioa: ZIO[R,E,A], ziob: ZIO[R,E,B]): ZIO[R,E,B] = for {
        _ <- zioa
        b <- ziob
    } yield b
        // zioa.flatMap(_ => {
        //     ziob.map(x => x)
        // })

    // 2. - sequence two ZIOs and take value of the first one
    def sequenceTakeFirst[R,E,A,B](zioa: ZIO[R,E,A], ziob: ZIO[R,E,B]): ZIO[R,E,A] = for {
        a <- zioa
        _ <- ziob
    } yield a
        // zioa.flatMap(a => {
        //     ziob.map(_ => a)
        // })

    // 3. - Run a ZIO forever
    def runForever[R,E,A](zio: ZIO[R,E,A]): ZIO[R,E,A] = 
        zio.flatMap(_ => runForever(zio))

    val endlessLoop = runForever {
        ZIO.succeed {
            println("running...")
            Thread.sleep(1000)
        }
    }

    // 4. - covnert the value of a ZIO to something else
    def convert[R,E,A,B](zioa: ZIO[R,E,A], value: B): ZIO[R,E,B] = 
        zioa.map(_ => value)

    // 5. - discard the value of a ZIO to Unit
    def discard[R,E,A](zioa: ZIO[R,E,A]): ZIO[R,E,Unit] = 
        zioa.map(_ => ())

    // 6. - Recursion 
    def sum(n: Int): Int = 
        if(n == 0) 0
        else n + sum(n - 1) // crash sum(20000)
    
    def sumZIO(n: Int): UIO[Int] = 
        if(n == 0) ZIO.succeed(0)
        else ZIO.succeed(n).flatMap(x => sumZIO(x - 1).map(_ + x))

    // 7. - fibonacci
    // hint: use ZIO.suspend/ZIO.suspendSucceed
    def fiboZIO(n: Int): UIO[BigInt] = 
        if(n == 0) ZIO.succeed(0)
        else if(n == 1) ZIO.succeed(1)
        else ZIO.suspendSucceed(fiboZIO(n - 1)).flatMap(x => fiboZIO(n - 2).map(y => x + y))

    @main def base(): Unit = {
        val runtime = Runtime.default
        given trace: Trace = Trace.empty
        val zioa = ZIO.succeed({println("should return: 10"); 10})
        val ziob = ZIO.succeed({println("should return: 20"); 20})
        // println(sum(20000)) // Crash
        Unsafe.unsafeCompat { u => 
            given uns: zio.Unsafe = u
            val mol = runtime.unsafe.run(
                // sequenceTakeLast(zioa, ziob)
                // sequenceTakeFirst(zioa, ziob)
                // endlessLoop
                // convert(zioa, 30)
                // discard(zioa)
                // sumZIO(20000)
                fiboZIO(30)
            )
            println(mol)
        }
    }
}
