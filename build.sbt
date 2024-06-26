val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "dev.zio" %% "zio" % "2.0.22"
    )
  )
