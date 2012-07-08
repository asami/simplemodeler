seq(conscriptSettings :_*)

name := "simplemodeler"

organization := "org.simplemodeling"

version := "0.4.0-SNAPSHOT"

scalaVersion := "2.9.1"

// crossScalaVersions := Seq("2.9.1", "2.9.2")

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

resolvers += Classpaths.typesafeResolver

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.9.1"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.4"

libraryDependencies += "org.goldenport" %% "goldenport" % "0.4.0-SNAPSHOT"

libraryDependencies += "org.goldenport" %% "goldenport-scalaz-lib" % "0.1.2"

libraryDependencies += "org.goldenport" %% "goldenport-record" % "0.1.1"

libraryDependencies += "org.smartdox" %% "smartdox" % "0.3.0-SNAPSHOT"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

//
publishTo := Some(Resolver.file("asamioffice", file("target/maven-repository")))
