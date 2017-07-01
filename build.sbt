name := "sangria-akka-http-example"
version := "0.1.0-SNAPSHOT"

description := "An example GraphQL server written with akka-http and sangria."

scalaVersion := "2.12.1"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "1.1.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1",

  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)
Revolver.settings
enablePlugins(DockerPlugin)


dockerfile in docker := {
  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    from("java")
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}
