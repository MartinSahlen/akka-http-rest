val postgreSqlVersion = "9.4.1207"
val postgreSql = "org.postgresql" % "postgresql" % postgreSqlVersion :: Nil

val postgreSqlAsync =  "com.github.mauricio" %% "postgresql-async" % "0.2.18" :: Nil

val flywayVersion = "3.2.1"
val flyway = "org.flywaydb" % "flyway-core"% flywayVersion :: Nil

val log4jVersion: String = "2.5"
val loggingDeps = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.14",
  "com.typesafe.scala-logging" %% "scala-logging"     % "3.1.0",
  "org.apache.logging.log4j"    % "log4j-api"         % log4jVersion,
  "org.apache.logging.log4j"    % "log4j-core"        % log4jVersion,
  "org.apache.logging.log4j"    % "log4j-slf4j-impl"  % log4jVersion
)

// Jackson is needed to configure Log4j using JSON
val jacksonVersion: String = "2.7.1"
val jackson = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion :: Nil

val testingDeps = Seq(
  "org.scalatest" % "scalatest_2.11"  % "3.0.0-M15"   % "test",
  "junit"         % "junit"           % "4.12"    % "test",
  "org.hamcrest"  % "hamcrest-all"    % "1.3"     % "test",
  "com.h2database" % "h2" % "1.4.191"
)

val joda = "com.github.nscala-time" % "nscala-time_2.11" % "2.8.0" :: Nil

val akkaVersion = "2.4.2-RC2"

val akka = Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)

val accord = "com.wix" %% "accord-core" % "0.5" :: Nil

val swagger = "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.6.2" :: Nil

libraryDependencies ++=
    accord      ++
    akka        ++
    flyway      ++
    jackson     ++
    joda        ++
    loggingDeps ++
    postgreSql  ++
    postgreSqlAsync ++
    swagger     ++
    testingDeps
