name := "webcrank-password"

scalaVersion := "2.10.1"

crossScalaVersions := Seq("2.9.2", "2.9.3", "2.10.1")

releaseSettings

useGpg := true

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.0",
  "org.scalaz" %% "scalaz-effect" % "7.0.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.owtelse.codec" % "base64" % "1.0.4",
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
)

libraryDependencies <+= scalaVersion.apply(ver => {
  val specs = if (ver startsWith "2.9") "1.12.4.1" else "1.14"
  "org.specs2" %% "specs2" % specs % "test"
})

resolvers ++= Seq(
  "oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "oss releases" at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions <++= scalaVersion.map(ver => {
  val scala_2_9 = Seq(
    "-Ydependent-method-types"
  )
  val scala_2_10 = Seq(
    "-Yinline-warnings",
    "-Yno-adapted-args",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:postfixOps"
  )
  val all = Seq(
    "-deprecation",
    "-unchecked",
    "-optimise",
    "-Ywarn-value-discard",
    "-Ywarn-all",
    "-Xfatal-warnings"
  )
  all ++ (if (ver startsWith "2.9") scala_2_9 else scala_2_10)
})
