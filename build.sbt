seq(conscriptSettings :_*)

name := "simplemodeler"

organization := "org.simplemodeling"

version := "0.4.0-RC2"

scalaVersion := "2.9.2"

// crossScalaVersions := Seq("2.9.2", "2.9.1")

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

resolvers += Classpaths.typesafeResolver

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-compiler" % _ }

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.4"

libraryDependencies += "org.goldenport" %% "goldenport" % "0.4.2-SNAPSHOT"

libraryDependencies += "org.goldenport" %% "goldenport-scalaz-lib" % "0.2.0"

libraryDependencies += "org.goldenport" %% "goldenport-record" % "0.2.0"

libraryDependencies += "org.smartdox" %% "smartdox" % "0.3.1-SNAPSHOT"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

//
publishTo := Some(Resolver.file("asamioffice", file("target/maven-repository")))
