name := "akka-http-blog"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Maven" at "https://repo1.maven.org/maven2/"

herokuFatJar in Compile := Some((assemblyOutputPath in assembly).value)
