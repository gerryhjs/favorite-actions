import org.jetbrains.intellij.tasks.PatchPluginXmlTask

group = "org.dandoh.favacts"
version = "0.0.1"

val pluginGroup = group
val pluginVersion = version

val isCI = !System.getenv("CI").isNullOrBlank()

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
  }
}

fun fromToolbox(root: String, ide: String) = file(root)
    .resolve(ide)
    .takeIf { it.exists() }
    ?.resolve("ch-0")
    ?.listFiles()
    .orEmpty()
    .asSequence()
    .filterNotNull()
    .filter { it.isDirectory }
    .filterNot { it.name.endsWith(".plugins") }
    .maxBy {
      val (major, minor, patch) = it.name.split('.')
      String.format("%5s%5s%5s", major, minor, patch)
    }
    ?.also { println("Picked: $it") }

plugins {
  java
  id("org.jetbrains.intellij") version "0.4.10"
  kotlin("jvm") version "1.3.50"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

intellij {
  updateSinceUntilBuild = false
  instrumentCode = true
  val user = System.getProperty("user.name")
  val os = System.getProperty("os.name")
  val root = when {
    os.startsWith("Windows") -> "C:\\Users\\$user\\AppData\\Local\\JetBrains\\Toolbox\\apps"
    os == "Linux" -> "/home/$user/.local/share/JetBrains/Toolbox/apps"
    else -> return@intellij
  }
  val intellijPath = sequenceOf("IDEA-C-JDK11", "IDEA-C", "IDEA-JDK11", "IDEA-U")
      .mapNotNull { fromToolbox(root, it) }.firstOrNull()
  intellijPath?.absolutePath?.let { localPath = it }
  val pycharmPath = sequenceOf("PyCharm-C", "IDEA-C-JDK11", "IDEA-C", "IDEA-JDK11", "IDEA-U")
      .mapNotNull { fromToolbox(root, it) }.firstOrNull()
  pycharmPath?.absolutePath?.let { alternativeIdePath = it }

}

tasks.withType<PatchPluginXmlTask> {
  changeNotes(file("info/change-notes.html").readText())
  pluginDescription(file("info/description.html").readText())
  version(pluginVersion)
  pluginId(pluginGroup)
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  compile(kotlin("stdlib-jdk8"))
  testCompile(kotlin("test-junit"))
  testCompile("junit", "junit", "4.12")
}
