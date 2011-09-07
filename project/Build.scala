import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "com.twitter"
  val buildVersion      = "2.3.11-SNAPSHOT"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
	publishMavenStyle := true,
	publishTo := Some(Resolver.file("Local", Path.userHome / "projects" / "maven2" asFile)(Patterns(true, Resolver.mavenStyleBasePattern))),
	scalacOptions += "-deprecation"
  )
}

object Resolvers {
	val myGithub =  "btd" 			at "http://btd.github.com/maven2"
	val javaNet = "java.net"        at "http://download.java.net/maven/2"
}

object UtilBuild extends Build {
  import Resolvers._
  import BuildSettings._

  // Sub-project specific dependencies
  val defaultDeps = Seq (
  "com.twitter"         %% "util-core"        % "1.11.2-SNAPSHOT",
  "commons-dbcp"        % "commons-dbcp"      % "1.4",
  "commons-pool"        % "commons-pool"      % "1.5.4",
  "com.jolbox"          % "bonecp"            % "0.7.1.RELEASE",
  "com.h2database"      % "h2"                % "1.3.158"
  )

  lazy val core = Project (
    "querulous-b",
    file ("querulous-core"),
    settings = buildSettings ++ Seq (resolvers ++= Seq(myGithub, javaNet), libraryDependencies ++= defaultDeps)
  )
}