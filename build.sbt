import AssemblyKeys._

assemblySettings

name := "kinesis-client-library-start"

version := "1.0"
     
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.amazonaws" % "amazon-kinesis-client" % "1.6.1"
)
