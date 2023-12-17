ThisBuild / version := "1.1.5"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "MetalScala3",
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
    libraryDependencies += "org.openjfx" % "javafx" % "21.0.1" pomOnly (),
    libraryDependencies += "org.openjfx" % "javafx-fxml" % "21",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.17",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test",
    libraryDependencies += "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"
  )
