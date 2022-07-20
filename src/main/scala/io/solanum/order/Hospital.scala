package io.solanum.order

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors

/** @author
 *    Chenyu Liu
 *  @since 7/20/22
 *    Wednesday
 */

object Hospital {

  sealed trait Command
  case class ChildBorn(listing: Receptionist.Listing) extends Command
  case object GetAllKids                              extends Command

  def apply(children: Set[ActorRef[Child.Command]] = Set.empty): Behavior[Command] =
    Behaviors.setup[Command] { ctx =>
      val msgAdaptor = ctx.messageAdapter[Receptionist.Listing](l => ChildBorn(l))
      ctx.system.receptionist ! Receptionist.Find(Child.SK, msgAdaptor)
      Behaviors.receiveMessage[Command] {
        case ChildBorn(Child.SK.Listing(l)) =>
          apply(l)
        case GetAllKids =>
          ctx.log.info(s"""
              |[HOSPITAL]
              |${children.map(i => i.path).mkString("\n")}
              |""".stripMargin)
          Behaviors.same
      }
    }

}
