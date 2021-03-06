import com.typesafe.sbt.site.asciidoctor.AsciidoctorPlugin
import sbt.Keys.{autoScalaLibrary, _}
import sbt._
import sbtassembly.AssemblyPlugin.defaultShellScript

ThisBuild / organization := "org.mmadt"
ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1-SNAPSHOT"
Compile / compileOrder := CompileOrder.JavaThenScala
makeSite := {(makeSite in machine).value}
scmInfo := Some(ScmInfo(url("https://github.com/mm-adt/vm"), "scm:git:git@github.com:mm-adt/vm.git"))
enablePlugins(GhpagesPlugin)
git.remoteRepo := scmInfo.value.get.connection.replace("scm:git:", "")

lazy val machine = (project in file("machine"))
  .settings(
    name := "machine",
    description := "mm-ADT :: JVM Machine",
    crossPaths := false,
    autoScalaLibrary := false,
    mainClass in assembly := Some("org.mmadt.language.console.Console"),
    assemblyJarName in assembly := s"mmadt-vm-${version.value}.jar",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript)),
    libraryDependencies := List(
      "org.jline" % "jline" % "3.13.3",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"))
  .enablePlugins(AssemblyPlugin)
  .enablePlugins(AsciidoctorPlugin)


