package io.solanum.order

import akka.actor.typed.{ Behavior, PreRestart }
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.DurationInt

/** @author
 *    Chenyu Liu
 *  @since 7/20/22
 *    Wednesday
 */

object Child {
  sealed trait Command
  case object Cry extends Command

  def apply(childName: String): Behavior[Command] =
    Behaviors.setup[Command] { ctx =>
      Behaviors.withTimers[Command] { timer =>
        timer.startTimerAtFixedRate(Cry, 1.second)
        Behaviors
          .receiveMessage[Command] { case Cry =>
            ctx.log.info(s"$childName crying: Wraaaaaaaaa!!!!!!")
            Behaviors.same
          }
      }
    }
}
