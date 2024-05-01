package Effect

import zio._
import zio.Cause.Empty

object ZIOEffect {

    // 1. - sequence two ZIOs and take value of the last one
    def sequenceTakeLast[R,E,A,B](zioa: ZIO[R,E,A], ziob: ZIO[R,E,B]): ZIO[R,E,B] = ???
    // 2. - sequence two ZIOs and take value of the first one
    def sequenceTakeFirst[R,E,A,B](zioa: ZIO[R,E,A], ziob: ZIO[R,E,B]): ZIO[R,E,A] = ???
    // 3. - Run a ZIO forever
    def runForever[R,E,A](zio: ZIO[R,E,A]): ZIO[R,E,A] = ???
    // val endlessLoop = runForever {
    //     ZIO.succeed {
    //         println("running...")
    //         Thread.sleep(1000)
    //     }
    // }
    // 4. - covnert the value of a ZIO to something else
    def convert[R,E,A,B](zioa: ZIO[R,E,A], value: B): ZIO[R,E,B] = ???
    // 5. - discard the value of a ZIO to Unit
    def discard[R,E,A](zioa: ZIO[R,E,A]): ZIO[R,E,Unit] = ???

    @main def base(): Unit = {
        val runtime = Runtime.default
        given trace: Trace = Trace.empty
        val zioa = ZIO.succeed(10)
        val ziob = ZIO.succeed(20)
        Unsafe.unsafeCompat { u => 
            given uns: zio.Unsafe = u
            val mol = runtime.unsafe.run(
                // sequenceTakeLast(zioa, ziob)
                zioa
            )
            println(mol)
        }
    }
}
