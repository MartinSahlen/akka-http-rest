
val slickVersion = "3.1.1"
val slick = "com.typesafe.slick" %% "slick" % slickVersion :: Nil

val akkaVersion = "2.4.2-RC2"

val akka = Seq(
  "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)

val postgreSqlVersion = "9.4.1207"
val postgreSql = "org.postgresql" % "postgresql" % postgreSqlVersion :: Nil

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
val jackson = Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
)

val testingDeps = Seq(
  "org.scalatest" % "scalatest_2.11"  % "3.0.0-M15"   % "test",
  "junit"         % "junit"           % "4.12"    % "test",
  "org.hamcrest"  % "hamcrest-all"    % "1.3"     % "test",
  "com.h2database" % "h2" % "1.4.191"
)

val joda = "com.github.nscala-time" % "nscala-time_2.11" % "2.8.0" :: Nil

val akkaHttpVersion = "2.0.3"

val akkaHttp = Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpVersion
)

val accord = "com.wix" %% "accord-core" % "0.5" :: Nil

libraryDependencies ++=
    accord      ++
    akka        ++
    akkaHttp    ++
    flyway      ++
    jackson     ++
    joda        ++
    loggingDeps ++
    postgreSql  ++
    slick       ++
    testingDeps