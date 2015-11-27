name := "akka-rest-api"

version := "1.0"

scalaVersion := "2.11.7"

herokuFatJar in Compile := Some((assemblyOutputPath in assembly).value)