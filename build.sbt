name := """myapp"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  javaWs,
  javaCore,
  javaJdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.mybatis" % "mybatis" % "3.3.0",
  "com.google.code.gson" % "gson" % "2.3.1",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.17",
  "com.amazonaws" % "aws-java-sdk-core" % "1.10.17",
  "commons-io" % "commons-io" % "2.4",
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "net.sf.jmimemagic" % "jmimemagic" % "0.1.4"
)

// Add app folder as resource directory so that mapper xml files are in the classpath
unmanagedResourceDirectories in Compile <+= baseDirectory( _ / "app/resources" )

// but filter out java and html files that would then also be copied to the classpath
excludeFilter in Compile in unmanagedResources := "*.java" || "*.html"

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true
