seq(conscriptSettings :_*)

name := "simplemodeler"

organization := "org.simplemodeling"

version := "0.3.3"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

resolvers += Classpaths.typesafeResolver

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.9.1"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.3"

libraryDependencies += "org.goldenport" %% "goldenport" % "0.3.2"

libraryDependencies += "org.goldenport" %% "goldenport-scalaz-lib" % "0.1.2-SNAPSHOT"

libraryDependencies += "org.goldenport" %% "goldenport-record" % "0.1.0"

libraryDependencies += "org.smartdox" %% "smartdox" % "0.2.3"

libraryDependencies += "org.scalatest" % "scalatest_2.9.1" % "1.6.1" % "test"

libraryDependencies += "junit" % "junit" % "4.7" % "test"

//
publishTo := Some(Resolver.file("asamioffice", file("target/maven-repository")))
