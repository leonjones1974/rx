
name in ThisBuild := "rx"
organization in ThisBuild := "uk.camsw"
scalaVersion in ThisBuild := "2.12.4"
version in ThisBuild := "6.0.0-SNAPSHOT"

val scalatestVersion = "3.0.4"
val scalacheckVersion = "1.13.5"
val pegdownVersion = "1.6.0"
val shapelessScalacheckVersion = "0.6.1"

(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/scalatest-report")
logBuffered in Test := false

resolvers in ThisBuild += Resolver.sonatypeRepo("releases")


lazy val rxJavaTestDsl = (project in file("rxjava-test-dsl"))
  .settings(libraryDependencies ++= dependencies ++ testDependencies)

lazy val rxScalaTestDsl = (project in file("rxscala-test-dsl"))
  .dependsOn(rxJavaTestDsl)
  .settings(libraryDependencies ++= dependencies ++ testDependencies)


val dependencies: Seq[ModuleID] = Seq(
  "org.assertj" % "assertj-core" % "3.2.0",
  "com.jayway.awaitility" % "awaitility" % "1.6.5",
  "junit" % "junit" % "4.12",
  "io.reactivex" % "rxjava" % "1.0.14",
  "com.google.guava" % "guava" % "18.0",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "io.reactivex" % "rxscala_2.12.0-M5" % "0.26.3",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

val testDependencies: Seq[ModuleID] = Seq(
  "org.pegdown" % "pegdown" % pegdownVersion % "test",
  "org.scalacheck" %% "scalacheck" % scalacheckVersion % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6",
  "org.typelevel" % "shapeless-scalacheck_2.12" % shapelessScalacheckVersion % "test",
  "org.scalatest" %% "scalatest" % scalatestVersion
)


publishTo in ThisBuild := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".sonatype" / ".credentials")

publishMavenStyle := true
publishArtifact in Test := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false
publishArtifact := false

pomExtra in ThisBuild := (
  <url>http://camsw.uk/rx</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://www.opensource.org/licenses/bsd-license.php</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:leonjones1974/rx.git</url>
      <connection>scm:git:git@github.com:leonjones1974/rx.git</connection>
    </scm>
    <developers>
      <developer>
        <id>leonjones1974</id>
        <name>Leon Jones</name>
        <url>http://camsw.uk</url>
      </developer>
    </developers>)

