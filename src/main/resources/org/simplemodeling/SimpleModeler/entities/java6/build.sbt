name := "sample"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "com.google.inject" % "guice" % "3.0"

libraryDependencies += "org.hibernate" % "hibernate-validator" % "4.2.0.Final"

libraryDependencies += "org.json" % "json" % "20090211"

libraryDependencies += "org.hibernate.java-persistence" % "jpa-api" % "2.0-cr-1"

libraryDependencies += "org.hibernate" % "hibernate-entitymanager" % "4.0.1.Final"

libraryDependencies += "org.simplemodeling" % "simplemodeler-runtime" % "0.1-SNAPSHOT"
