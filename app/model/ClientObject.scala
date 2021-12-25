package model

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import akka.pattern.ask
import model.Common.{ResponseMsg, SendToClient}

import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object ClientObject extends StrictLogging {
  implicit val timeout: Timeout = Timeout(3000.milliseconds)

  val configFile: String = getClass.getClassLoader.getResource("client.conf").getFile
  val config: Config = ConfigFactory.parseFile(new File(configFile))
  val system: ActorSystem = ActorSystem("client-system", config)
  val clientActor: ActorRef = system.actorOf(Props[ClientActor], name = "client")

  def sendNumberToRemote(n: Long): Either[Throwable, String] =
    try {
      val f = clientActor ? SendToClient(n)
      Await.ready(f, timeout.duration)
      f.value.get match {
        case Success(ResponseMsg(s: String)) =>
          logger.info(s"ClientObject : Success : Right($s)")
          Right(s)
        case Failure(e) =>
          logger.info(s"ClientObject : Failure : Left($e)")
          Left(e)
        case _ =>
          logger.info(s"ClientObject : ??? : Right(Unknown)")
          Right("Unknown")
      }
    } catch {
      case e: Exception =>
        logger.info(s"ClientObject : Exception : Left($e)")
        Left(e)
    }
}
