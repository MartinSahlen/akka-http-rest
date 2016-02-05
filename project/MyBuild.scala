import sbt._

object MyBuild extends Build {

  lazy val root = Project("root", file(".")) dependsOn swaggerProject
  lazy val swaggerProject = RootProject(uri("https://github.com/Tecsisa/akka-http-swagger.git"))

}