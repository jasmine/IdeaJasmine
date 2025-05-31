import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(17)
}

dependencies {
    intellijPlatform {
        webstorm("2025.1")
        bundledPlugins("JavaScript", "JavaScriptDebugger")
        testFramework(TestFrameworkType.Platform)
    }

    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.18.0")
}

intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")
        description = providers.gradleProperty("pluginDescription")

        ideaVersion {
            sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
        }
    }
}