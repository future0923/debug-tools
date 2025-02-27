import org.jetbrains.intellij.tasks.BuildSearchableOptionsTask

fun properties(key: String) = providers.gradleProperty(key)

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "io.github.future0923"
version = "3.4.1"

repositories {
    mavenLocal()
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}

dependencies {
    implementation("io.github.future0923:debug-tools-common:3.4.1")
    implementation("io.github.future0923:debug-tools-client:3.4.1")
    implementation("cn.hutool:hutool-http:5.8.29")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set("DebugTools")
    version.set("2024.3")
//    version.set("2024.2")
//    version.set("2024.1")
//    version.set("2023.3")
//    version.set("2023.2")
//    version.set("2023.1")
    type.set("IU")
    plugins.set(listOf("com.intellij.java", "com.intellij.modules.json"))
//    plugins.set(listOf("com.intellij.java"))
//    plugins = ["java"]
}

tasks {
    withType<JavaCompile> {
//        getOptions().setEncoding("UTF-8")
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    withType<BuildSearchableOptionsTask> {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("243.*")
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

tasks.named("build") {
    finalizedBy("movePluginZip")
}

tasks.named("buildPlugin") {
    finalizedBy("movePluginZip")
}

tasks.named("clean") {
    finalizedBy("cleanPluginZip")
}