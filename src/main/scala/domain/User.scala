package domain


case class User(email: String, password: String, roles: Seq[String])
