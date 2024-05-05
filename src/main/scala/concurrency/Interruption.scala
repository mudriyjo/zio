package concurrency

import zio._

object Interruption extends ZIOAppDefault {

  /**
   + * 1. Move Debug extensions into package level
   * 2. Implement interrupt example
   * 3. Implement onInterrupt method to handle interruption
   * 4. Implement fork interruption, that interruption not wait when forked fiber is finished
   * 5. Implement example with automatic interruption (main/child)
   * 6. implement example how to avoid interruption using forkDaemon
   * 7. Implement example with race interruption
   */
//  val zioWithTime =
  /**
   * Exercises
   * 1. Implement a timeout function that will interrupt the fiber after a given duration.
   * 2. Implement a timeout v2 function
   */
  // 1. we interrupt fiber if duration is out
  def timeout[R,E,A](zio: ZIO[R,E,A], duration: Duration): ZIO[R, E, A] = ???

  //2. timeout v2
  def timeout2[R,E,A](zio: ZIO[R,E,A], duration: Duration): ZIO[R, E, Option[A]] = ???
  override def run = ???
}
