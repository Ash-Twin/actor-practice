package io.solanum.order

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors

/** @author
 *    Chenyu Liu
 *  @since 7/20/22
 *    Wednesday
 */

object User {

  sealed trait Command

  case class Greeting(greet: String, replyTo: ActorRef[Command]) extends Command

  case class GreetingNoReply(greet: String) extends Command

  case class Birth(childName: String) extends Command

  case class GetKids(replyTo: ActorRef[Set[ActorRef[Child.Command]]]) extends Command

  def apply(userName: String, childs: Set[ActorRef[Child.Command]] = Set.empty): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      Behaviors.receiveMessage[Command] {

        case Greeting(greet, replyTo) =>
          context.log.info(greet)
          replyTo ! GreetingNoReply(s"Oh nice to meet you! I am $userName")
          Behaviors.same
        case GreetingNoReply(greet) =>
          context.log.info(greet)
          Behaviors.same
        case Birth(childName) =>
          val child = context.spawn(Child(s"$userName-$childName"), childName)
          apply(userName, childs + child)
        case GetKids(replyTo) =>
          replyTo ! childs
          Behaviors.same
      }
    }

}
