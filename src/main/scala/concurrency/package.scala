import zio.ZIO

package object concurrency {
  extension [R,E,A](zio: ZIO[R,E,A])
    def debuggingTask: ZIO[R,E,A] =
      zio
        .tap(value => ZIO.succeed(println(s"${Thread.currentThread()}[Success]: ${value}")))
        .tapErrorCause(err => ZIO.succeed(println(s"${Thread.currentThread()}[Failed]: ${err}")))
}
