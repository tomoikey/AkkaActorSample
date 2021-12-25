package model

import akka.actor.{Actor, ActorSelection}
import akka.util.Timeout
import akka.pattern.ask
import com.typesafe.scalalogging.StrictLogging
import model.ClientObject.system.dispatcher
import model.Common.{SendToClient, SendToRemote}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class ClientActor extends Actor with StrictLogging {
  implicit val timeout: Timeout = Timeout(2000.milliseconds)
  val remoteActor: ActorSelection = context.actorSelection(ClientObject.config.getString("app.remote-app.remote-actor"))

  override def receive: Receive = {
    case SendToClient(send: Long) =>
      try {
        val tempSender = sender
        val f = remoteActor ? SendToRemote(send)
        f.onComplete {
          case Success(s) =>
            logger.info(s"ClientActor : Success : $s")
            tempSender ! s
          case Failure(e) =>
            logger.info(s"ClientActor : Failure : $e")
            throw e
        }
        Await.ready(f, timeout.duration)
      } catch {
        case e: Exception =>
          logger.info(s"ClientActor : Exception : $e")
          throw e
      }
    case any =>
      logger.info(s"Unknown message $any from $sender")
  }
}
