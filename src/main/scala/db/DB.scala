package db

import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContext, Future}
import com.github.mauricio.async.db.{ RowData, QueryResult }

trait DB {
  implicit def actorSystem : ActorSystem
  implicit def executionContext : ExecutionContext

  lazy val pool = new Pool().pool

  def execute(query: String, values: Any*): Future[QueryResult] = {
    if (values.nonEmpty)
      pool.sendPreparedStatement(query, values)
    else
      pool.sendQuery(query)
  }

  def fetch(query: String, values: Any*): Future[Option[Seq[RowData]]] =
    execute(query, values: _*).map(_.rows)

}
