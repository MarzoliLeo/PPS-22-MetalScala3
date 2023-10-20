ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "MetalScala3"
  )

libraryDependencies += "org.openjfx" % "javafx" % "21.0.1" pomOnly()
libraryDependencies += "org.openjfx" % "javafx-fxml" % "21"
