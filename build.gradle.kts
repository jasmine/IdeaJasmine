plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.14.2"
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set(properties["ideaVersion"] as String)
    pluginName.set(properties["name"] as String)
    plugins.set(listOf("JavaScript", "CSS"))
    updateSinceUntilBuild.set(false)
}

fun latestReleaseNotesFile(): File {
    return fileTree("release_notes")
        .getFiles()
        .sorted()
        .last()
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        changeNotes.set(provider { latestReleaseNotesFile().readText() })
    }
    
    prepareSandbox {
        from("src/main/resources/intellij_reporter.js") {
            into("${intellij.pluginName.get()}/lib/")
        }
    }

}

repositories {
    mavenCentral()
}