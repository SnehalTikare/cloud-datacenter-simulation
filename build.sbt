name := "CloudSimulation_HW1"

version := "0.1"

scalaVersion := "2.13.3"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "com.typesafe" % "config" % "1.4.0"
libraryDependencies += "org.cloudsimplus" % "cloudsim-plus" % "5.1.0"