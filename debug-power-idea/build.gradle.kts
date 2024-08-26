fun properties(key: String) = providers.gradleProperty(key)

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "io.github.future0923"
version = "2.2.0"

repositories {
    mavenLocal()
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}

dependencies {
    implementation("io.github.future0923:debug-power-common:2.2.0")
    implementation("io.github.future0923:debug-power-client:2.2.0")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set("DebugPower")
    version.set("2024.1")
    type.set("IU")
    plugins.set(listOf("com.intellij.java"))
//    plugins = ["java"]
}

tasks {
    withType<JavaCompile> {
//        getOptions().setEncoding("UTF-8")
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("242.*")
    }
}