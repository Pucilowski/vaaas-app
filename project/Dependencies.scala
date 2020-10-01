import sbt._

object Dependencies {

  val loggingDependencies = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.codehaus.janino" % "janino" % "3.1.2"
  )

  object aecor {
    val org = "io.aecor"
    val v = "0.18.2"
    val pgJournalV = "0.3.0"

    val core = org %% "core" % v

    def apply() = Seq(
      core,
      "io.aecor" %% "schedule" % v,
      "io.aecor" %% "akka-cluster-runtime" % v,
      "io.aecor" %% "distributed-processing" % v,
      "io.aecor" %% "boopickle-wire-protocol" % v,
      "io.aecor" %% "aecor-postgres-journal" % pgJournalV,
      "io.aecor" %% "test-kit" % v % Test
    )
  }

  object akka {
    val v = "2.5.18"
//    val v = "2.6.9"

    val slf4j = "com.typesafe.akka" %% "akka-slf4j" % v
  }

  object boopickle {
    val v = "1.3.0"
//    val v = "1.3.3"

    val shapeless = "io.suzaku" %% "boopickle-shapeless" % v
  }

  object cats {
    private val org = "org.typelevel"
    private val version = "2.2.0"
    private val effectVersion = "2.2.0"

    val core = org %% "cats-core" % version
    val effect = org %% "cats-effect" % effectVersion
  }

  val chimney = {
    val version = "0.5.3"

    "io.scalaland" %% "chimney" % version
  }

  object circe {
    val v = "0.13.0"

    val core = "io.circe" %% "circe-core" % v
    val parser = "io.circe" %% "circe-parser" % v
    val generic = "io.circe" %% "circe-generic" % v
    val genericExtras = "io.circe" %% "circe-generic-extras" % v

    val optics = "io.circe" %% "circe-optics" % v
    val literal = "io.circe" %% "circe-literal" % v

    val jawn = "io.circe" %% "circe-jawn" % v
  }

  object doobie {
    val v = "0.6.0"
//    val v = "0.7.0"
//    val v = "0.9.2"

    val core = "org.tpolecat" %% "doobie-core" % v
    val postgres = "org.tpolecat" %% "doobie-postgres" % v
    val hikari = "org.tpolecat" %% "doobie-hikari" % v
  }

  object enumeratum {
    val v = "1.6.1"

    val core = "com.beachape" %% "enumeratum" % v
    val circe = "com.beachape" %% "enumeratum-circe" % v
  }

  object flyway {
    private val v = "6.5.6"

    val core = "org.flywaydb" % "flyway-core" % v
  }

  object http4s {
    val v = "0.21.7"

    val dsl = "org.http4s" %% "http4s-dsl" % v
    val blazeServer = "org.http4s" %% "http4s-blaze-server" % v
    val blazeClient = "org.http4s" %% "http4s-blaze-client" % v
    val circe = "org.http4s" %% "http4s-circe" % v
  }

  object log4cats {
    lazy val org = "io.chrisdavenport"
    lazy val v = "1.1.1"

    val core = org %% "log4cats-core" % v
    val slf4j = org %% "log4cats-slf4j" % v
  }

  object monix {
    val v = "3.2.2"
  }

  object pureconfig {
    private val org = "com.github.pureconfig"
    private val v = "0.13.0"

    val core = org %% "pureconfig" % v
    val generic = org %% "pureconfig-generic" % v
    val http4s = org %% "pureconfig-http4s" % v
    val yaml = org %% "pureconfig-yaml" % v
  }

  val quicklens = {
    val version = "1.4.12"

    "com.softwaremill.quicklens" %% "quicklens" % version
  }

  object scalatest {
    lazy val v = "3.2.2"
  }

  val squants = {
    val version = "1.7.0"
    "org.typelevel" %% "squants" % version
  }

  object sttp {

    object client {
      private val org = "com.softwaremill.sttp.client"
      private val v = "2.2.4"

      val circe = org %% "circe" % v
      val zioBackend = org %% "async-http-client-backend-zio" % v
    }

    object tapir {
      private val org = "com.softwaremill.sttp.tapir"
      private val v = "0.16.16"

      val core = org %% "tapir-core" % v

      val zio = org %% "tapir-zio" % v
      val ziohttp4sServer = org %% "tapir-zio-http4s-server" % v

      val jsonCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % v

      val apiDocs = Seq(
        org %% "tapir-openapi-docs" % v,
        org %% "tapir-openapi-circe-yaml" % v,
        org %% "tapir-swagger-ui-http4s" % v
      )
    }

  }

  object tsec {
    private val version = "0.2.1"

    def apply: Seq[ModuleID] = Seq(password, cipherJca)

    val common = "io.github.jmcardon" %% "tsec-common" % version
    val password = "io.github.jmcardon" %% "tsec-password" % version
    val cipherJca = "io.github.jmcardon" %% "tsec-cipher-jca" % version
  }

  object zio {
    private val org = "dev.zio"
    private val version = "1.0.1"
    private val interopCatsVersion = "2.1.4.0"
    private val telemetryVersion = "0.1.0"

    val core = org %% "zio" % version

    val macros = org %% "zio-macros" % version

    val test = org %% "zio-test" % version % Test
    val testSbt = org %% "zio-test-sbt" % version % Test
    val testMagnolia = org %% "zio-test-magnolia" % version % Test

    val cats = org %% "zio-interop-cats" % interopCatsVersion

    val telemetry = org %% "zio-telemetry" % telemetryVersion
  }

}
