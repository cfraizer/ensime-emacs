import sbt._
import bintray.Keys._
import com.typesafe.sbt.SbtGit._
import java.io.FileNotFoundException
import java.io.File
import sbt.IO

import AssemblyKeys._

organization := "org.ensime"

name := "ensime"

scalaVersion := "2.9.3"

git.baseVersion := "1.0"

// rolling release has the git hash in the version
versionWithGit

libraryDependencies <<= scalaVersion { scala_version => Seq(
  "org.apache.lucene"          %  "lucene-core"          % "3.5.0",
  "org.sonatype.tycho"         %  "org.eclipse.jdt.core" % "3.6.2.v_A76_R36x",
  "asm"                        %  "asm-commons"          % "3.3.1",
  "asm"                        %  "asm-util"             % "3.3.1",
  "com.googlecode.json-simple" %  "json-simple"          % "1.1.1" intransitive(),
  "org.scalatest"              %% "scalatest"            % "1.9.2" % "test",
  "org.scalariform"            %% "scalariform"          % "0.1.4",
  "org.scala-lang"             %  "scala-compiler"       % scala_version,
  "org.scala-refactoring"      %% "org.scala-refactoring.library" % "0.6.2"
)}

val JavaTools = {
  List[File](
    new File(Option(System.getenv("JAVA_HOME")).getOrElse("/tmp") + "/lib/tools.jar"),
    new File(new File(System.getProperty("java.home")).getParent + "/lib/tools.jar")
  ).filter(_.exists).headOption.getOrElse(
    throw new FileNotFoundException("tools.jar")
  )
}

internalDependencyClasspath in Compile += { Attributed.blank(JavaTools)}

scalacOptions in Compile ++= Seq(
  "-encoding", "UTF-8" //, "-Xfatal-warnings"
)

javacOptions in (Compile, compile) ++= Seq (
  "-source", "1.6", "-target", "1.6", "-Xlint:all", //"-Werror",
  "-Xlint:-options", "-Xlint:-path", "-Xlint:-processing"
)

javacOptions in doc ++= Seq("-source", "1.6")

assemblySettings

test in assembly := {}

artifact in (Compile, assembly) ~= { art =>
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)

// not working: https://github.com/sbt/sbt-assembly/issues/117
mainClass in assembly := Some("org.ensime.server.Server")

bintrayPublishSettings

licenses += ("BSD", url("http://opensource.org/licenses/BSD-3-Clause"))

bintrayOrganization in bintray := Some("ensime")

net.virtualvoid.sbt.graph.Plugin.graphSettings

// TODO: tests should fail if anything is reformatted
//scalariformSettings

//instrumentSettings