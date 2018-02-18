import Dependencies._

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    inThisBuild(List(
      organization := "emil-capstone.nyu.edu",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "aws-interface",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.amazonaws" % "aws-java-sdk-ec2" % "1.11.274"
    ),
  )
