lazy val root = (project in file(".")).
  settings(
    name := "scala-solution",
    mainClass in (Compile, run) := Some("tutorial.Main"),
    version := "1.1",
    scalaVersion := "2.11.7",
    libraryDependencies += "org.codecraftgame" %% "codecraft" % "0.6.1"
  )



