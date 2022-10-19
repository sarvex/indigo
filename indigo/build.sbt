import scala.language.postfixOps
import Misc._

Global / onChangedBuildSource := ReloadOnSourceChanges

val scala3Version = "3.2.0"

ThisBuild / versionScheme                                  := Some("early-semver")
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / scalaVersion                                   := scala3Version

lazy val indigoVersion = IndigoVersion.getVersion
// For the docs site
lazy val indigoDocsVersion  = "0.14.0"
lazy val tyrianDocsVersion  = "0.6.0"
lazy val scalaJsDocsVersion = "1.11.0"
lazy val scalaDocsVersion   = "3.2.0"
lazy val sbtDocsVersion     = "1.7.1"
lazy val millDocsVersion    = "0.10.7"

lazy val commonSettings: Seq[sbt.Def.Setting[_]] = Seq(
  version            := indigoVersion,
  crossScalaVersions := Seq(scala3Version),
  organization       := "io.indigoengine",
  libraryDependencies ++= Dependencies.commonSettings.value,
  testFrameworks += new TestFramework("munit.Framework"),
  Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  scalacOptions ++= Seq("-language:strictEquality"),
  scalafixOnCompile := true,
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision,
  autoAPIMappings   := true,
  logo              := name.value
)

lazy val neverPublish = Seq(
  publish / skip      := true,
  publishLocal / skip := true
)

lazy val publishSettings = {
  import xerial.sbt.Sonatype._
  Seq(
    publishTo              := sonatypePublishToBundle.value,
    publishMavenStyle      := true,
    sonatypeProfileName    := "io.indigoengine",
    licenses               := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    sonatypeProjectHosting := Some(GitHubHosting("PurpleKingdomGames", "indigo", "indigo@purplekingdomgames.com")),
    developers := List(
      Developer(
        id = "davesmith00000",
        name = "David Smith",
        email = "indigo@purplekingdomgames.com",
        url = url("https://github.com/davesmith00000")
      )
    )
  )
}

// Root
lazy val indigoProject =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin, ScalaUnidocPlugin)
    .settings(
      neverPublish,
      commonSettings,
      name        := "IndigoProject",
      code        := codeTaskDefinition,
      usefulTasks := customTasksAliases,
      presentationSettings(version),
      ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(sandbox, perf, docs)
    )
    .aggregate(indigo, indigoExtras, indigoJsonCirce, sandbox, perf, docs, benchmarks, shaders)

// Testing

lazy val sandbox =
  project
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .dependsOn(indigoExtras)
    .dependsOn(indigoJsonCirce)
    .settings(
      neverPublish,
      commonSettings,
      name                := "sandbox",
      showCursor          := true,
      title               := "Sandbox",
      gameAssetsDirectory := "assets",
      disableFrameRateLimit := (sys.props("os.name").toLowerCase match {
        case x if x contains "windows" => false
        case _                         => true
      }),
      electronInstall := (sys.props("os.name").toLowerCase match {
        case x if x.contains("windows") || x.contains("linux") =>
          indigoplugin.ElectronInstall.Version("^18.0.0")

        case _ =>
          indigoplugin.ElectronInstall.Global
      })
    )

lazy val perf =
  project
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .dependsOn(indigoExtras)
    .dependsOn(indigoJsonCirce)
    .settings(
      neverPublish,
      commonSettings,
      name                := "indigo-perf",
      showCursor          := true,
      title               := "Perf",
      gameAssetsDirectory := "assets",
      windowStartWidth    := 800,
      windowStartHeight   := 600,
      disableFrameRateLimit := (sys.props("os.name").toLowerCase match {
        case x if x contains "windows" => false
        case _                         => true
      }),
      electronInstall := (sys.props("os.name").toLowerCase match {
        case x if x.contains("windows") || x.contains("linux") =>
          indigoplugin.ElectronInstall.Version("^18.0.0")

        case _ =>
          indigoplugin.ElectronInstall.Global
      })
    )

// Indigo Extensions
lazy val indigoExtras =
  project
    .in(file("indigo-extras"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(indigo)
    .settings(
      name := "indigo-extras",
      libraryDependencies ++= Dependencies.indigoExtras.value,
      commonSettings ++ publishSettings,
      Compile / sourceGenerators += shaderLibGen("ExtrasShaderLibrary", "indigoextras.shaders").taskValue
    )

// Indigo
lazy val indigo =
  project
    .in(file("indigo"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "indigo",
      commonSettings ++ publishSettings,
      Compile / sourceGenerators += shadersGen.taskValue,
      Compile / sourceGenerators += shaderLibGen("ShaderLibrary", "indigo.shaders").taskValue,
      libraryDependencies ++= Dependencies.indigo.value
    ).dependsOn(shaders)

// Shader
lazy val shaders =
  project
    .in(file("indigo-shaders"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "indigo-shaders",
      commonSettings ++ publishSettings,
      Compile / sourceGenerators += shaderDSLGen.taskValue,
    )

// Circe
lazy val indigoJsonCirce =
  project
    .in(file("indigo-json-circe"))
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(indigo)
    .settings(
      name := "indigo-json-circe",
      commonSettings ++ publishSettings,
      libraryDependencies ++= Dependencies.indigoJsonCirce.value
    )

lazy val benchmarks =
  project
    .in(file("benchmarks"))
    .enablePlugins(ScalaJSPlugin, JSDependenciesPlugin)
    .dependsOn(indigoExtras)
    .dependsOn(indigoJsonCirce)
    .settings(
      neverPublish,
      name         := "indigo-benchmarks",
      version      := indigoVersion,
      organization := "io.indigoengine",
      Test / test  := {},
      libraryDependencies ++= Dependencies.benchmark.value,
      jsDependencies ++= Dependencies.benchmarkJs.value
    )

lazy val jsdocs = project
  .settings(
    neverPublish,
    organization := "io.indigoengine",
    libraryDependencies ++= Dependencies.jsDocs.value,
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "indigo-json-circe"    % indigoDocsVersion,
      "io.indigoengine" %%% "indigo"               % indigoDocsVersion,
      "io.indigoengine" %%% "indigo-extras"        % indigoDocsVersion,
      "io.indigoengine" %%% "tyrian-io"            % tyrianDocsVersion,
      "io.indigoengine" %%% "tyrian-indigo-bridge" % tyrianDocsVersion
    )
  )
  .enablePlugins(ScalaJSPlugin)

lazy val docs = project
  .in(file("indigo-docs"))
  .enablePlugins(MdocPlugin)
  .settings(
    neverPublish,
    organization       := "io.indigoengine",
    mdocJS             := Some(jsdocs),
    mdocExtraArguments := List("--no-link-hygiene"),
    mdocVariables := Map(
      "VERSION"         -> indigoDocsVersion,
      "SCALAJS_VERSION" -> scalaJsDocsVersion,
      "SCALA_VERSION"   -> scalaDocsVersion,
      "SBT_VERSION"     -> sbtDocsVersion,
      "MILL_VERSION"    -> millDocsVersion,
      "js-opt"          -> "fast"
    )
  )
  .settings(
    run / fork := true
  )

addCommandAlias(
  "gendocs",
  List(
    "cleanAll",
    "unidoc",   // Docs in ./target/scala-3.2.0/unidoc/
    "docs/mdoc" // Docs in ./indigo/indigo-docs/target/mdoc
  ).mkString(";", ";", "")
)

def shadersGen =
  shadersCodeGen("shaders", files => ShaderGen.makeShader(files, _))

def shaderLibGen(module: String, path: String) =
  shadersCodeGen("shader-library", files => ShaderLibraryGen.makeShaderLibrary(module, path, files, _))

def shadersCodeGen(dir: String, makeFiles: Set[File] => File => Seq[File]) = Def.task {
  val cachedFun = FileFunction.cached(streams.value.cacheDirectory / dir) { (files: Set[File]) =>
    makeFiles(files)((Compile / sourceManaged).value).toSet
  }
  cachedFun(IO.listFiles(baseDirectory.value / dir).toSet).toSeq
}

def shaderDSLGen = Def.task {
  ShaderDSLGen.makeShaderDSL((Compile / sourceManaged).value)
}
