import sbt.Keys.{ parallelExecution, scalacOptions, _ }
import sbt._
import Dependencies.{ _ }

lazy val catsMTLVersion = "0.4.0"

//lazy val circeGenericExtrasVersion = "0.12.2"

lazy val logbackVersion = "1.2.3"
lazy val metaParadiseVersion = "3.0.0-M11"
lazy val scalaCheckVersion = "1.14.0"
//lazy val shapelessVersion = "2.3.3"

lazy val simulacrum = "com.github.mpilquist" %% "simulacrum" % "0.12.0"
//lazy val kindProjector = compilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3" cross CrossVersion.full)
//lazy val kindProjector = compilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3" cross CrossVersion.full)
//addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3" cross CrossVersion.full)
//addCompilerPlugin("org.typelevel" % "kind-projector_2.13" % "0.10.3")
//lazy val kindProjector = compilerPlugin("org.typelevel" % "kind-projector" % "0.10.3")

lazy val betterMonadicFor = addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
lazy val scalapbRuntime =
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"

//compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")
//addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")
lazy val kindProjector = compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

//addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full)

/**
 * Common
 */

lazy val commonModel = Project(id = "common-model", base = file("common/common-model"))

lazy val commonPersistence = Project(id = "common-persistence", base = file("common/common-persistence")).settings(
  baseSettings,
  betterMonadicFor,
  fork := true,
  libraryDependencies ++= Seq(
    circe.core,
    circe.generic,
    circe.parser,
    doobie.core,
    doobie.postgres,
    doobie.hikari,
    flyway.core,
    zio.core,
    zio.cats
  )
)

lazy val commonEventsourced = Project(id = "common-eventsourced", base = file("common/common-eventsourced")).settings(
  baseSettings,
  betterMonadicFor,
  libraryDependencies ++= Seq(
  )
)

lazy val commonApi = Project(id = "common-api", base = file("common/common-api"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      circe.generic,
      sttp.tapir.core,
      sttp.tapir.zio,
      sttp.tapir.jsonCirce,
      tsec.common
    )
  )
  .dependsOn(commonModel)

/**
 * SaaS Bounded Context
 */
lazy val saasModel = Project(id = "saas-model", base = file("saas/saas-model"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      circe.core,
      circe.genericExtras,
      squants,
      tsec.password,
      zio.core
    )
  )
  .dependsOn(commonModel)

lazy val saasService = Project(id = "saas-service", base = file("saas/saas-service"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      cats.core,
      cats.effect,
      zio.cats
    ) ++ loggingDependencies
  )
  .dependsOn(saasModel, commonPersistence)

lazy val saasApi = Project(id = "saas-api", base = file("saas/saas-api"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      chimney
    )
  )
  .dependsOn(saasService, commonApi)

/*lazy val commonDomain = Project(id = "common-domain", base = file("common/common-domain"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "squants" % squantsVersion,
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsVersion,
    ),
    //sources in(Compile, doc) := Nil, // macroparadise doesn't work with scaladoc yet.
  )*/

lazy val commonInfra = Project(id = "common-infra", base = file("common/common-infra")).settings(
  baseSettings,
  betterMonadicFor,
  fork := true,
  libraryDependencies ++= Seq(
    scalapbRuntime,
    squants,
    doobie.core,
    doobie.postgres,
    doobie.hikari,
    circe.core,
    circe.generic,
    circe.parser
  ),
  //sources in(Compile, doc) := Nil, // macroparadise doesn't work with scaladoc yet.
  PB.targets in Compile := Seq(
    scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
  )
)

/**
 * Auctions Bounded Context
 */

lazy val auctionsModel = Project(id = "auctions-model", base = file("auctions/auctions-model"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      enumeratum.core,
      enumeratum.circe
    )
  )
  .dependsOn(saasModel)

lazy val auctionsViewModel = Project(id = "auctions-view-model", base = file("auctions/auctions-view-model"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(auctionsModel)

lazy val auctionsEventsourced = Project(id = "auctions-eventsourced", base = file("auctions/auctions-eventsourced"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= aecor() ++ Seq(
      kindProjector,
      compilerPlugin(("org.scalameta" % "paradise" % metaParadiseVersion).cross(CrossVersion.full)),
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      scalapbRuntime,
      cats.core,
      cats.effect,
      log4cats.core,
      log4cats.slf4j,
      http4s.dsl,
      http4s.blazeServer,
      http4s.blazeClient,
      http4s.circe,
      circe.core,
      circe.generic,
      circe.genericExtras,
      circe.parser,
      boopickle.shapeless,
      akka.slf4j,
      quicklens,
      "org.scalatest" %% "scalatest" % scalatest.v % Test,
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test,
      zio.cats
    ),
    scalacOptions += "-Xplugin-require:macroparadise",
    sources in (Compile, doc) := Nil, // macroparadise doesn't work with scaladoc yet.
    PB.targets in Compile := Seq(
      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
    )
  )
  .dependsOn(auctionsModel, auctionsViewModel, commonEventsourced, commonInfra)

lazy val auctionsApi = Project(id = "auctions-api", base = file("auctions/auctions-api"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      http4s.dsl,
      http4s.blazeServer,
      http4s.blazeClient,
      http4s.circe
    )
  )
  .dependsOn(auctionsEventsourced, saasApi, commonApi)

/*lazy val forexService = Project(id = "forex-service", base = file("forex/forex-service"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      compilerPlugin("org.scalameta" % "paradise" % metaParadiseVersion cross CrossVersion.full),
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      scalapbRuntime,
      "io.monix" %% "monix" % "3.0.0-RC2",

      http4s.dsl, http4s.blazeServer, http4s.blazeClient, http4s.circe,

      circe.core,
      circe.generic,
      circe.parser,

      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-http4s" % pureConfigVersion,
      quicklens,

      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test
    ),
    sources in(Compile, doc) := Nil, // macroparadise doesn't work with scaladoc yet.
    mainClass in Compile := Some("io.vaaas.forex.App"),
  )
  .enablePlugins(DockerPlugin, JavaAppPackaging)*/

lazy val blogPost = Project(id = "blogpost", base = file("blogpost")).settings(
  libraryDependencies ++= Seq(
    aecor.core,
    cats.core,
    enumeratum.core,
    enumeratum.circe,
    squants
  )
)

lazy val app = Project(id = "app", base = file("app"))
  .settings(
    baseSettings,
    betterMonadicFor,
    fork := true,
    libraryDependencies ++= Seq(
      http4s.dsl,
      http4s.blazeServer,
      http4s.blazeClient,
      http4s.circe,
      circe.core,
      circe.generic,
      circe.parser,
      pureconfig.core,
      pureconfig.http4s,
      quicklens,
      sttp.tapir.ziohttp4sServer,
      zio.core,
      zio.cats
    ) ++ sttp.tapir.apiDocs
    //    sources in(Compile, doc) := Nil, // macroparadise doesn't work with scaladoc yet.
    //    mainClass in Compile := Some("aughaus.App"),
    //    PB.targets in Compile := Seq(
    //      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
    //    ),
  )
  .dependsOn(
    saasApi,
    auctionsApi
  )
  .enablePlugins(DockerPlugin, JavaAppPackaging)

lazy val baseSettings = Seq(
  scalaVersion in ThisBuild := "2.12.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
    //    Resoliver.bintrayRepo("ovotech", "maven")
  ),
  scalacOptions in (Compile, console) ~= {
    _.filterNot(unusedWarnings.toSet + "-Ywarn-value-discard")
  },
  scalacOptions ++= commonScalacOptions,
  //  scalacOptions ++= Seq("-Xmax-classfile-name", "128"),
  parallelExecution in Test := false,
  sources in (Compile, doc) := Nil,
  dockerExposedPorts := Seq(9000),
  dockerBaseImage := "java:8.161",
  publishTo := None,
  cancelable in Global := true
)

lazy val commonScalacOptions = Seq(
  //  "-deprecation",
  //  "-encoding",
  //  "UTF-8",
  //  "-feature",
  //  "-language:existentials",
  "-language:higherKinds",
  //  "-language:implicitConversions",
  //  "-language:experimental.macros",
  //  "-unchecked",
  //  "-Xfatal-warnings",
  //  "-Xlint",
  //  "-Ywarn-dead-code",
  //  "-Ywarn-numeric-widen",
  //  "-Ywarn-value-discard",
  "-Ypartial-unification"
) ++ unusedWarnings

//lazy val unusedWarnings = Seq.empty[String]
lazy val unusedWarnings = Seq("-Ywarn-unused" /*, "-Ywarn-unused-import"*/ )
