name := "ocrdemo"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")

scalacOptions += "-Ypartial-unification" // 2.11.9+

lazy val doobieVersion = "0.5.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2" % doobieVersion
)

libraryDependencies ++= Seq(
  "com.github.austinv11" % "Discord4J" % "2.7.0",
  "org.typelevel" %% "cats-effect" % "0.8",
  "net.sourceforge.tess4j" % "tess4j" % "3.4.3",
  "org.apache.lucene" % "lucene-suggest" % "7.2.1",
  "com.monovore" %% "decline" % "0.4.0",
  "co.fs2" %% "fs2-core" % "0.10.1",
  "com.lihaoyi" %% "fastparse" % "1.0.0"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)
