package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.projectRoots.Sdk;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author future0923
 */
public class DcevmUtils {

    public static Installation of(String jdkPathString) {
        if (DebugToolsStringUtils.isBlank(jdkPathString)) {
            return null;
        }
        Path jdkPath = Paths.get(jdkPathString);
        try {
            return new Installation(ConfigurationInfo.current(), jdkPath);
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean isDcevmInstalledLikeAltJvm(String jdkPathString) {
        Installation installation = of(jdkPathString);
        if (installation == null) {
            return false;
        }
        return installation.isDCEInstalledAltjvm();
    }

    public static String getJdkVersion(String jdkPathString) {
        Installation installation = of(jdkPathString);
        if (installation == null) {
            return null;
        }
        return installation.getVersion();
    }
    public static boolean isDCEVMPresent(String jdkPathString) {
        Installation installation = of(jdkPathString);
        if (installation == null) {
            return false;
        }
        return installation.isDCEInstalled() || installation.isDCEInstalledAltjvm();
    }

    public static String determineDCEVMVersion(Sdk projectSdk) {
        String jdkPathString = projectSdk.getHomePath();
        if (jdkPathString == null) {
            return null;
        }
        Path jdkPath = Paths.get(jdkPathString);
        Installation installation = new Installation(ConfigurationInfo.current(), jdkPath);
        if (installation.isDCEInstalled()) {
            return installation.getVersionDcevm();
        } else if (installation.isDCEInstalledAltjvm()) {
            return installation.getVersionDcevmAltjvm();
        }
        return null;
    }

    @Getter
    public enum ConfigurationInfo {

        // Note: 32-bit is not supported on Mac OS X
        MAC_OS(null, "bsd_amd64_compiler2",
                "lib/client", "lib/server", "lib/dcevm", "lib/server", "lib/dcevm",
                "bin/java", "libjvm.dylib") {
            @Override
            public String[] paths() {
                return new String[] { "/Library/Java/JavaVirtualMachines/" };
            }
        },
        LINUX("linux_i486_compiler2", "linux_amd64_compiler2",
                "lib/i386/client", "lib/i386/server", "lib/i386/dcevm", "lib/amd64/server", "lib/amd64/dcevm",
                "bin/java", "libjvm.so") {
            @Override
            public String[] paths() {
                return new String[]{"/usr/java", "/usr/lib/jvm"};
            }
        },
        WINDOWS("windows_i486_compiler2", "windows_amd64_compiler2",
                "bin/client", "bin/server", "bin/dcevm", "bin/server", "bin/dcevm",
                "bin/java.exe", "jvm.dll") {
            @Override
            public String[] paths() {
                return new String[]{
                        System.getenv("JAVA_HOME") + "/..",
                        System.getenv("PROGRAMW6432") + "/JAVA",
                        System.getenv("PROGRAMFILES") + "/JAVA",
                        System.getenv("PROGRAMFILES(X86)") + "/JAVA",
                        System.getenv("SYSTEMDRIVE") + "/JAVA"};
            }
        };

        private final String resourcePath32;
        private final String resourcePath64;

        private final String clientPath;
        private final String server32Path;
        private final String dcevm32Path;
        private final String server64Path;
        private final String dcevm64Path;

        private final String javaExecutable;
        private final String libraryName;

        ConfigurationInfo(String resourcePath32, String resourcePath64,
                          String clientPath,
                          String server32Path, String dcevm32Path,
                          String server64Path, String dcevm64Path,
                          String javaExecutable, String libraryName) {
            this.resourcePath32 = resourcePath32;
            this.resourcePath64 = resourcePath64;
            this.clientPath = clientPath;
            this.server32Path = server32Path;
            this.dcevm32Path = dcevm32Path;
            this.server64Path = server64Path;
            this.dcevm64Path = dcevm64Path;
            this.javaExecutable = javaExecutable;
            this.libraryName = libraryName;
        }

        public String getResourcePath(boolean bit64) {
            return bit64 ? resourcePath64 : resourcePath32;
        }

        public String getServerPath(boolean bit64) {
            return bit64 ? server64Path : server32Path;
        }

        public String getBackupLibraryName() {
            return libraryName + ".backup";
        }

        public String[] paths() {
            return new String[0];
        }

        public static ConfigurationInfo current() {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("windows")) {
                return ConfigurationInfo.WINDOWS;
            } else if (os.contains("mac") || os.contains("darwin")) {
                return ConfigurationInfo.MAC_OS;
            } else if (os.contains("unix") || os.contains("linux")) {
                return ConfigurationInfo.LINUX;
            }
            throw new IllegalStateException("OS is unsupported: " + os);
        }

        // Utility methods to query installation directories
        public boolean isJRE(Path directory) {
            if (Files.isDirectory(directory)) {
                if (!Files.exists(directory.resolve(getJavaExecutable()))) {
                    return false;
                }

                Path client = directory.resolve(getClientPath());
                if (Files.exists(client)) {
                    if (!Files.exists(client.resolve(getLibraryName()))) {
                        return Files.exists(client.resolve(getBackupLibraryName()));
                    }
                }

                Path server = directory.resolve(getServer64Path());
                if (Files.exists(server)) {
                    if (!Files.exists(server.resolve(getLibraryName()))) {
                        return Files.exists(server.resolve(getBackupLibraryName()));
                    }
                }
                return true;
            }
            return false;
        }

        public boolean isJDK(Path directory) {
            if (Files.isDirectory(directory)) {
                Path jreDir = directory.resolve(getJREDirectory());
                return isJRE(jreDir);
            }
            return false;
        }

        public String executeJava(Path jreDir, String... params) throws IOException {
            Path executable = jreDir.resolve(getJavaExecutable());
            String[] commands = new String[params.length + 1];
            System.arraycopy(params, 0, commands, 1, params.length);
            commands[0] = executable.toAbsolutePath().toString();
            Process p = Runtime.getRuntime().exec(commands);

            StringBuilder result = new StringBuilder();
            try (InputStream in = p.getErrorStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                    result.append('\n');
                }
            }
            return result.toString();
        }

        public boolean isDCEInstalled(Path dir, boolean altjvm) {
            Path jreDir;
            if (isJDK(dir)) {
                jreDir = dir.resolve("jre");
            } else {
                jreDir = dir;
            }

            if (altjvm) {
                Path altvm32Path = jreDir.resolve(getDcevm32Path());
                Path altvm64Path = jreDir.resolve(getDcevm64Path());

                return Files.exists(altvm32Path) || Files.exists(altvm64Path);
            } else {
                Path clientPath = jreDir.resolve(getClientPath());
                Path clientBackup = clientPath.resolve(getBackupLibraryName());

                Path serverPath = jreDir.resolve(getServer32Path());
                if (!Files.exists(serverPath)) {
                    serverPath = jreDir.resolve(getServer64Path());
                }
                Path serverBackup = serverPath.resolve(getBackupLibraryName());

                if (Files.exists(clientPath) && Files.exists(serverPath)) {
                    if (Files.exists(clientBackup) != Files.exists(serverBackup)) {
                        throw new IllegalStateException(jreDir.toAbsolutePath() + " has invalid state.");
                    }
                }
                return Files.exists(clientBackup) || Files.exists(serverBackup);
            }
        }

        public String getVersionString(Path jreDir, boolean altjvm) {
            try {
                if (altjvm) {
                    return executeJava(jreDir,  "-XXaltjvm=dcevm", "-version");
                } else {
                    return executeJava(jreDir, "-version");
                }
            } catch (Throwable e) {
                return e.getMessage();
            }
        }

        public boolean is64Bit(Path jreDir) {
            String versionString = getVersionString(jreDir, false);
            return versionString.contains("64-Bit") || versionString.contains("amd64");
        }

        public String getJavaVersion(Path jreDir) {
            return getVersionHelper(jreDir, ".*(?:java|openjdk) version.*\"(.*)\".*", true, false);
        }

        final public String getDCEVersion(Path jreDir, boolean altjvm) {
            return getVersionHelper(jreDir, ".*Dynamic Code Evolution.*build ([^,]+),.*", false, altjvm);
        }

        private String getVersionHelper(Path jreDir, String regex, boolean javaVersion, boolean altjvm) {
            String version = getVersionString(jreDir, altjvm);
            version = version.replaceAll("\n", "");
            Matcher matcher = Pattern.compile(regex).matcher(version);

            if (!matcher.matches()) {
                return "Could not get " + (javaVersion ? "java" : "dce") +
                        "version of " + jreDir.toAbsolutePath() + ".";
            }

            version = matcher.replaceFirst("$1");
            return version;
        }

        public String getJREDirectory() {
            return "jre";
        }
    }

    public static class Installation extends Observable {

        private final Path file;
        private final ConfigurationInfo config;

        private final boolean isJDK;

        // DCEVM is installed over default JVM (either server or client)
        private boolean installed;

        // DCEVM is installed as an alternative DCEVM (in separate dcevm directory)
        private boolean installedAltjvm;

        // version of Java
        private String version;
        // version of DCEVM in main location (client/server)
        private String versionDcevm;
        // version of DCEVM in alternative location (dcevm altjvm)
        private String versionDcevmAltjvm;

        private boolean is64Bit;

        public Installation(ConfigurationInfo config, Path path) {
            this.config = config;
            try {
                file = path.toRealPath();
            } catch (IOException ex) {
                throw new IllegalArgumentException(path.toAbsolutePath() + " is no JRE or JDK-directory.");
            }

            isJDK = config.isJDK(file);
            if (!isJDK && !config.isJRE(file)) {
                throw new IllegalArgumentException(path.toAbsolutePath() + " is no JRE or JDK-directory.");
            }

            version = config.getJavaVersion(file);
            update();
        }

        final public void update() {
            installed = config.isDCEInstalled(file, false);
            versionDcevm = installed ? config.getDCEVersion(file, false) : "";

            installedAltjvm = config.isDCEInstalled(file, true);
            versionDcevmAltjvm = installedAltjvm ? config.getDCEVersion(file, true) : "";

            is64Bit = config.is64Bit(file);
        }

        public Path getPath() {
            return file;
        }

        public String getVersion() {
            return version;
        }

        public String getVersionDcevm() {
            return versionDcevm;
        }

        public String getVersionDcevmAltjvm() {
            return versionDcevmAltjvm;
        }

        public boolean isJDK() {
            return isJDK;
        }

        public boolean is64Bit() {
            return is64Bit;
        }

        public void installDCE(boolean altjvm) throws IOException {
            new Installer(config).install(file, is64Bit, altjvm);
            update();
            setChanged();
            notifyObservers();
        }

        public void uninstallDCE() throws IOException {
            new Installer(config).uninstall(file, is64Bit);
            installed = false;
            installedAltjvm = false;
            update();
            setChanged();
            notifyObservers();
        }

        public boolean isDCEInstalled() {
            return installed;
        }

        public boolean isDCEInstalledAltjvm() {
            return installedAltjvm;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Installation other = (Installation) obj;
            return !(this.file != other.file && (this.file == null || !this.file.equals(other.file)));
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + (this.file != null ? this.file.hashCode() : 0);
            return hash;
        }
    }

    public static class Installer {

        private ConfigurationInfo config;

        public Installer(ConfigurationInfo config) {
            this.config = config;
        }

        public void install(Path dir, boolean bit64, boolean altjvm) throws IOException {
            if (config.isJDK(dir)) {
                dir = dir.resolve(config.getJREDirectory());
            }

            if (!altjvm) {
                Path serverPath = dir.resolve(config.getServerPath(bit64));
                if (Files.exists(serverPath)) {
                    installClientServer(serverPath, bit64);
                }

                Path clientPath = dir.resolve(config.getClientPath());
                if (Files.exists(clientPath) && !bit64) {
                    installClientServer(clientPath, false);
                }
            } else {
                Path altjvmPath = dir.resolve(bit64 ? config.getDcevm64Path() : config.getDcevm32Path());
                if (!Files.exists(altjvmPath)) {
                    Files.createDirectory(altjvmPath);
                }
                installClientServer(altjvmPath, bit64);
            }
        }

        /**
         * Try to uninstall DCEVM from all locations (skip if not exists).
         */
        public void uninstall(Path dir, boolean bit64) throws IOException {
            if (config.isJDK(dir)) {
                dir = dir.resolve(config.getJREDirectory());
            }

            Path serverPath = dir.resolve(config.getServerPath(bit64));
            if (Files.exists(serverPath)) {
                uninstallClientServer(serverPath);
            }

            Path clientPath = dir.resolve(config.getClientPath());
            if (Files.exists(clientPath) && !bit64) {
                uninstallClientServer(clientPath);
            }

            Path dcevm32Path = dir.resolve(config.getDcevm32Path());
            if (Files.exists(dcevm32Path)) {
                Files.deleteIfExists(dcevm32Path.resolve(config.getLibraryName()));
                Files.deleteIfExists(dcevm32Path.resolve(config.getBackupLibraryName()));
                Files.delete(dcevm32Path);
            }

            Path dcevm64Path = dir.resolve(config.getDcevm64Path());
            if (Files.exists(dcevm64Path)) {
                Files.deleteIfExists(dcevm64Path.resolve(config.getLibraryName()));
                Files.deleteIfExists(dcevm64Path.resolve(config.getBackupLibraryName()));
                Files.delete(dcevm64Path);
            }

        }

        public List<Installation> listInstallations() {
            return scanPaths(config.paths());
        }

        public ConfigurationInfo getConfiguration() {
            return config;
        }

        private void installClientServer(Path path, boolean bit64) throws IOException {
            String resource = config.getResourcePath(bit64) + "/product/" + config.getLibraryName();

            Path library = path.resolve(config.getLibraryName());
            Path backup = path.resolve(config.getBackupLibraryName());

            // backup any existing library (assume original JVM file)
            if (Files.exists(library)) {
                Files.move(library, backup);
            }

            try {
                // install actual DCEVM file
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
                    if (in == null) {
                        throw new IOException("DCEVM not available for java at '" + path + "'. Missing resource " + resource);
                    }

                    Files.copy(in, library);
                }
            } catch (NullPointerException | IOException e) {
                // try to restore original file
                if (Files.exists(backup)) {
                    Files.move(backup, library, StandardCopyOption.REPLACE_EXISTING);
                }
                throw e;
            }
        }

        private void uninstallClientServer(Path path) throws IOException {
            Path library = path.resolve(config.getLibraryName());
            Path backup = path.resolve(config.getBackupLibraryName());

            if (Files.exists(backup)) {
                Files.delete(library);
                Files.move(backup, library);
            }
        }

        private List<Installation> scanPaths(String... dirPaths) {
            List<Installation> installations = new ArrayList<>();
            for (String dirPath : dirPaths) {
                Path dir = Paths.get(dirPath);
                if (Files.isDirectory(dir)) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                        scanDirectory(stream, installations);
                    } catch (Exception ex) {
                        // Ignore, try different directory
                        ex.printStackTrace();
                    }
                }
            }
            return installations;
        }

        private void scanDirectory(DirectoryStream<Path> stream, List<Installation> installations) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    if (config.isJDK(path) || config.isJRE(path)) {
                        try {
                            Installation inst = new Installation(config, path);
                            if (!installations.contains(inst)) {
                                installations.add(inst);
                            }
                        } catch (Exception ex) {
                            // FIXME: just ignore the installation for now..
                            ex.printStackTrace();
                        }
                    } else {
                        // in macOS/OSX we have more complicated strucuture of directories with JVM...
                        // for example it may be /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home
                        // so we will look deper in structure of passed folders hoping that we will find JVM ;-)
                        try {
                            scanDirectory(Files.newDirectoryStream(path),installations);
                        } catch (IOException ignore) {

                        }
                    }
                }
            }
        }

    }
}

