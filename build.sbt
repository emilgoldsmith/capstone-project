import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "nyu.edu",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "emil-capstone",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.amazonaws" % "aws-java-sdk" % "1.11.274"
    )
  )
