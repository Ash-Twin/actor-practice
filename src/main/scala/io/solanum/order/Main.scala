package io.solanum.order

import akka.Done
import akka.actor.typed.scaladsl.AskPattern.{ Askable, schedulerFromActorSystem }
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import io.solanum.order.Hospital.GetAllKids
import io.solanum.order.User.{ Birth, Greeting, GreetingNoReply }

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success }

/** @author
 *    Chenyu Liu
 *  @since 7/20/22
 *    Wednesday
 */

object Main {

  def main(args: Array[String]): Unit =
    ActorSystem.create(
      Behaviors.setup[Done] { ctx =>
        import ctx.system
        import ctx.executionContext
        implicit val timeout: Timeout = Timeout.durationToTimeout(5.seconds)

        val james: ActorRef[User.Command]    = ctx.spawn(User("James"), "James")
        val as3asddd: ActorRef[User.Command] = ctx.spawn(User("as3asddd"), "as3asddd")

        // Section 1: Simple usage of messaging between actors
        as3asddd ! GreetingNoReply("Anonymous greeting")
        as3asddd ! Greeting("Hello I am James", james)
        Thread.sleep(2000)
        // Section 2: Blocking ask
        val c = Await.result(
          james.ask(Greeting("Hello there???", _)),
          timeout.duration
        )
        c match {
          case GreetingNoReply(greet) =>
            ctx.log.info(greet)
          case _ =>
        }
        Thread.sleep(2000)
        // Section 3: Child Actor
        val hospital = ctx.spawn(Hospital.apply(), "Hospital")
        as3asddd ! Birth("hhm")
        james ! Birth("hhm")
        Thread.sleep(10000)
        // Section 4: Receptionist
        ctx.log.info("\n" + ctx.system.printTree)
        for {
          jamesKids   <- james.ask(User.GetKids)
          as3asddKids <- as3asddd.ask(User.GetKids)
        } yield (jamesKids ++ as3asddKids).foreach(kid => ctx.log.info(kid.path.name))
        hospital ! GetAllKids
        Behaviors.same
      },
      "Example"
    )

}
