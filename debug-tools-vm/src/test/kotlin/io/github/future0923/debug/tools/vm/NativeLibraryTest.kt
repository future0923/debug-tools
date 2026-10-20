/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.vm

import io.github.future0923.debug.tools.base.config.AgentConfig
import io.github.future0923.debug.tools.base.constants.ProjectConstants
import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.Properties
import java.util.concurrent.TimeUnit

/** 在独立 JVM 中隔离静态平台状态和原生库，避免污染开发机的 agent 缓存。 */
class NativeLibraryTest {
    @Test
    fun selectsLibraryForSupportedPlatformsAndAliases() {
        val cases = listOf(
            Triple("Linux", "aarch64", "libJniLibrary-arm64.so"),
            Triple("Linux", "arm64", "libJniLibrary-arm64.so"),
            Triple("Linux", "amd64", "libJniLibrary-x64.so"),
            Triple("Linux", "x86_64", "libJniLibrary-x64.so"),
            Triple("Mac OS X", "aarch64", "libJniLibrary-arm64.dylib"),
            Triple("Mac OS X", "arm64", "libJniLibrary-arm64.dylib"),
            Triple("Mac OS X", "x86_64", "libJniLibrary.dylib"),
            Triple("Windows 11", "amd64", "libJniLibrary-x64.dll")
        )
        for ((os, arch, expected) in cases) {
            val output = probe("select", listOf("-Ddebug.tools.test.os=$os", "-Ddebug.tools.test.arch=$arch"))
            assertTrue("$os/$arch: $output", output.contains("SELECTED:$expected"))
        }
    }

    @Test
    fun rejectsUnsupportedLinuxArchitecture() {
        val output = probe("select", listOf("-Ddebug.tools.test.os=Linux", "-Ddebug.tools.test.arch=riscv64"), expectedExit = 1)
        assertTrue(output, output.contains("unsupported Linux architecture: riscv64"))
    }

    @Test
    fun loadsNativeLibraryAndScansInstances() {
        assumeTrue("需显式开启原生冒烟测试", java.lang.Boolean.getBoolean("debug.tools.native.test"))
        assertTrue(probe("native").contains("NATIVE_OK"))
    }

    @Test
    fun replacesCachedLibraryFromAnotherArchitecture() {
        assumeTrue("需显式开启原生冒烟测试", java.lang.Boolean.getBoolean("debug.tools.native.test"))
        assertTrue(probe("stale-cache").contains("NATIVE_OK"))
    }

    private fun probe(mode: String, options: List<String> = emptyList(), expectedExit: Int = 0): String {
        val home = Files.createTempDirectory("debug-tools-native-test-").toFile()
        try {
            val log = File(home, "probe.log")
            val classpath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"))
            val java = File(System.getProperty("java.home"), "bin/java").absolutePath
            val process = ProcessBuilder(
                listOf(java, "-Duser.home=${home.absolutePath}") + options +
                    listOf("-cp", classpath, NativeLibraryProbe::class.java.name, mode)
            ).redirectErrorStream(true).redirectOutput(log).start()
            if (!process.waitFor(45, TimeUnit.SECONDS)) {
                process.destroyForcibly().waitFor()
                throw AssertionError("原生测试超时: ${log.readText()}")
            }
            val output = log.readText()
            assertEquals(output, expectedExit, process.exitValue())
            return output
        } finally {
            home.deleteRecursively()
        }
    }
}

/** 子进程通过真实加载入口验证资源选择、缓存迁移和 JNI/JVMTI 调用。 */
object NativeLibraryProbe {
    private class Sample

    @JvmStatic
    fun main(args: Array<String>) {
        if (args[0] == "select") {
            // JDK 8 也会按 os.name 选择文件系统；先初始化真实平台，再只模拟被测工具类。
            java.nio.file.FileSystems.getDefault()
            System.setProperty("os.name", System.getProperty("debug.tools.test.os"))
            System.setProperty("os.arch", System.getProperty("debug.tools.test.arch"))
            val method = JvmToolsUtils::class.java.getDeclaredMethod("getLibName")
            method.isAccessible = true
            println("SELECTED:${method.invoke(null)}")
            return
        }

        if (args[0] == "stale-cache") {
            // 同版本旧库也必须失效，防止只按版本复用已提取的文件。
            val directory = File(System.getProperty("user.home"), ProjectConstants.NAME).apply { mkdirs() }
            val invalidLibrary = File(directory, "DebugToolsJniLibrary-${ProjectConstants.VERSION}.so")
            invalidLibrary.writeText("invalid native library from another architecture")
            val properties = Properties().apply {
                setProperty("debug.tools.version", ProjectConstants.VERSION)
                setProperty("debug.tools.current.os.arch", if (DebugToolsOSUtils.isArm64()) "x86_64" else "aarch_64")
                setProperty("debug.tools.jni.library.path", invalidLibrary.absolutePath)
            }
            File(directory, "debug-tools-cache.properties").outputStream().use { properties.store(it, null) }
        }

        val sample = Sample()
        JvmToolsUtils.init()
        check(JvmToolsUtils.getInstances(Sample::class.java).any { it === sample })
        val vmTool = VmTool.getInstance()
        check(vmTool.getInstances(Sample::class.java, 1).size == 1)
        check(vmTool.getInstanceSize(sample) > 0)
        check(vmTool.countInstances(Sample::class.java) >= 1)
        check(vmTool.sumInstanceSize(Sample::class.java) >= vmTool.getInstanceSize(sample))
        check(vmTool.getAllLoadedClasses().contains(Sample::class.java))
        vmTool.forceGc()
        check(AgentConfig.INSTANCE.currentOsArch == DebugToolsOSUtils.arch())
        check(File(AgentConfig.INSTANCE.jniLibraryPath).name.contains(DebugToolsOSUtils.arch()))
        println("NATIVE_OK:${System.getProperty("java.version")}:${DebugToolsOSUtils.arch()}")
    }
}
