lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    inThisBuild(List(
      organization := "emil-capstone.nyu.edu",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "main",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka" % "1.0.0"
    ),
  )
