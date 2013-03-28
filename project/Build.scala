import sbt._
import Keys._

object Settings {
  val jvmVersion        = "1.7"
  val buildOrganization = "com.duramec"
  val buildVersion      = "0.1.2"
  val buildScalaVersion = Version.Scala
  val warnDeadCode      = false
  val buildSettings = Defaults.defaultSettings ++ Seq(
      organization       := buildOrganization,
      version            := buildVersion,
      scalaVersion       := buildScalaVersion,
      scalaBinaryVersion <<= scalaVersion.identity
   )
  
  import Resolvers._
   
  val defaultSettings = buildSettings ++ Seq(
    resolvers ++= Seq(
        typesafe,
        typesafeSnapshot,
        duramec,
        duramecSnapshot,
        mavenLocal),
    compileOrder in Compile := CompileOrder.JavaThenScala,
    compileOrder in Test := CompileOrder.Mixed,
    scalacOptions in (Compile, doc) ++= Seq(
        ("-target:jvm-" + jvmVersion),
        "-optimise",
        "-feature",
        "-deprecation",
        "-Ystruct-dispatch:invoke-dynamic",
        "-Yinline",
        "-Yclosure-elim",
        (if (warnDeadCode) "-Ywarn-dead-code" else "")
      ),
    javacOptions ++= Seq(
        "-source", jvmVersion,
        "-target", jvmVersion,
        "-deprecation"
      ),
    javacOptions in doc := Seq("-source", jvmVersion),
    parallelExecution in Test := false,
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      if (v.trim.endsWith("SNAPSHOT")) Some(Resolvers.duramecSnapshot)
      else Some(Resolvers.duramec)
    },
    crossPaths := false // disable version number in artifacts
  )
}
    
object Resolvers {
  val typeSafePrefix   = "http://repo.typesafe.com/typesafe"
  val duramecPrefix    = "repo.duramec.com"
  val typesafe         = "typesafe" at (typeSafePrefix + "/releases/")
  val typesafeSnapshot = "snapshot" at (typeSafePrefix + "/snapshots/")
  val ivyLocal         = "ivy-local" at "file://"+Path.userHome+"/.ivy2/local"
  val mavenLocal       = "maven-local" at "file://"+Path.userHome+"/.m2/repository"
  val duramec          = Resolver.sftp("duramec", duramecPrefix, "/releases")
  val duramecSnapshot  = Resolver.sftp("duramec-snapshots", duramecPrefix, "/snapshots")
}

object Dependencies {
  import Dependency._
  
  val core = Seq(
      jodaTime,
      scalaTest)
}

object Version {
  val Scala     = "2.10.1"
  val ScalaTest = "2.0.M5b"
}

object Dependency {
  import Version._
  
  val jodaTime     = "joda-time"                 % "joda-time"         % "2.1"     % "compile"
  val scalaTest    = "org.scalatest"             % "scalatest_2.10"    % ScalaTest % "test"
}

object TimeBuild extends Build {
  import java.io.File._
  import Settings._
  
  lazy val time = Project(
    id = "time",
    base = file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.core
      )
    )
}
