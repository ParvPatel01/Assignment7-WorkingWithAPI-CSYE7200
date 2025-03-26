ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"
val requestsVersion = "0.8.0"
val ujsonVersion = "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "assignment-API",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % requestsVersion,  // For HTTP requests
      "com.lihaoyi" %% "ujson" % ujsonVersion         // For JSON parsing
    )
  )
