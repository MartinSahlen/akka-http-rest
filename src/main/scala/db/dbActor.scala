package db

import akka.actor.Actor
import akka.actor.Actor.Receive

import scala.concurrent.ExecutionContext

/**
  * Created by Sahlen on 25/02/16.
  */
class dbActor extends Actor with DB {

  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case "maartin" => sender ! "LOL"
  }
}
