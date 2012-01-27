name := "simplemodeler"

organization := "org.simplemodeling"

version := "0.3.1"

scalaVersion := "2.9.1"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

resolvers += Classpaths.typesafeResolver

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.9.1"

libraryDependencies += "org.scalatest" % "scalatest_2.9.1" % "1.6.1" % "test"

libraryDependencies += "junit" % "junit" % "4.7" % "test"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.3"

libraryDependencies += "org.goldenport" %% "goldenport" % "0.3.1"

//
publishTo := Some(Resolver.file("asamioffice", file("target/maven-repository")))
