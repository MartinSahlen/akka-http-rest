package db

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}
import com.github.mauricio.async.db.{ RowData, QueryResult }


object DB {
  implicit val executionContext: ExecutionContext = global

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
