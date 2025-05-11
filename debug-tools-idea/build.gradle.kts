import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.BuildSearchableOptionsTask
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask.FailureLevel

buildscript {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
    }
}

plugins {
    idea
    id("java")
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

val pluginVersionString = prop("pluginVersion")
val ideVersion = prop("ideVersion")

group = "io.github.future0923"
version = pluginVersionString

val platformVersion = when {
    // e.g. '2024.1'
    ideVersion.length == 6 -> ideVersion.replace(".", "").substring(2).toInt()
    // e.g. '243.16718.32'
    else -> ideVersion.substringBefore(".").toInt()
}

allprojects {
    apply {
        plugin("idea")
        plugin("java")
        plugin("org.jetbrains.intellij.platform.module")
    }

    repositories {
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
        }
    }

    dependencies {
        intellijPlatform {
            intellijIdeaUltimate(ideVersion)
            // using "Bundled" to gain access to the Java plugin's test classes
            testFramework(TestFrameworkType.Bundled)

            bundledPlugin("com.intellij.java")

            bundledModule("com.intellij.modules.json")

            // 2024.3 extracted JSON support into a plugin
            if (platformVersion >= 243) {
                bundledPlugin("com.intellij.modules.json")
            }
            pluginVerifier(version = "1.385")
        }
        implementation("io.github.future0923:debug-tools-common:${version}")
        implementation("io.github.future0923:debug-tools-client:${version}")
        implementation("cn.hutool:hutool-http:5.8.29")
        compileOnly("org.projectlombok:lombok:1.18.32")
        annotationProcessor("org.projectlombok:lombok:1.18.32")
    }

    intellijPlatform {
        instrumentCode = false
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks {
        withType<JavaCompile> {
            sourceCompatibility = "17"
            targetCompatibility = "17"
            options.encoding = "UTF-8"
        }
        withType<BuildSearchableOptionsTask> {
            enabled = false
        }
        register<Copy>("movePluginZip") {
            from(layout.buildDirectory.dir("distributions"))
            into(layout.projectDirectory.dir("../dist"))
            include("*.zip")
        }
        register<Delete>("cleanPluginZip") {
            delete(fileTree(layout.projectDirectory.dir("../dist")) {
                include("*.zip") // 只删除 .zip 文件
            })
        }
    }
}

project(":") {
    apply {
        plugin("java")
        plugin("org.jetbrains.intellij.platform")
    }

    intellijPlatform {

        pluginConfiguration {

            version = pluginVersionString

            ideaVersion {
                sinceBuild.set(prop("sinceBuild"))
//                untilBuild.set(prop("untilBuild"))
            }
        }

        pluginVerification {
//
            ides {
                ides(prop("ideVersionVerifier").split(","))
            }

            failureLevel.set(
                listOf(
                    FailureLevel.INTERNAL_API_USAGES,
                    FailureLevel.COMPATIBILITY_PROBLEMS,
                    FailureLevel.OVERRIDE_ONLY_API_USAGES,
                    FailureLevel.NON_EXTENDABLE_API_USAGES,
                    FailureLevel.PLUGIN_STRUCTURE_WARNINGS,
                    FailureLevel.MISSING_DEPENDENCIES,
                    FailureLevel.INVALID_PLUGIN,
                )
            )
        }

    }
}

tasks.named("build") {
    finalizedBy("movePluginZip")
}

tasks.named("buildPlugin") {
    finalizedBy("movePluginZip")
}

fun prop(name: String): String {
    return extra.properties[name] as? String ?: error("Property `$name` is not defined in gradle.properties")
}