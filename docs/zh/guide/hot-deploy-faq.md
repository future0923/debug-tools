
# Q: 1. Caused by: java.lang.ClassNotFoundException: com.sun.tools.javac.processing.JavacProcessingEnvironment

```text
Caused by: io.github.future0923.debug.tools.server.compiler.DynamicCompilerException: Compilation Warnings
message: Can't initialize javac processor due to (most likely) a class loader problem: java.lang.NoClassDefFoundError: com/sun/tools/javac/processing/JavacProcessingEnvironment
  	at lombok.javac.apt.LombokProcessor.getJavacProcessingEnvironment(LombokProcessor.java:436)
  	at lombok.javac.apt.LombokProcessor.init(LombokProcessor.java:94)
  	at lombok.core.AnnotationProcessor$JavacDescriptor.want(AnnotationProcessor.java:160)
  	at lombok.core.AnnotationProcessor.init(AnnotationProcessor.java:213)
  	at lombok.launch.AnnotationProcessorHider$AnnotationProcessor.init(AnnotationProcessor.java:64)
  	at com.sun.tools.javac.processing.JavacProcessingEnvironment$ProcessorState.<init>(JavacProcessingEnvironment.java:500)
  	at com.sun.tools.javac.processing.JavacProcessingEnvironment$DiscoveredProcessors$ProcessorStateIterator.next(JavacProcessingEnvironment.java:597)
  	at com.sun.tools.javac.processing.JavacProcessingEnvironment.discoverAndRunProcs(JavacProcessingEnvironment.java:690)
  	at com.sun.tools.javac.processing.JavacProcessingEnvironment.access$1800(JavacProcessingEnvironment.java:91)
  	at com.sun.tools.javac.processing.JavacProcessingEnvironment$Round.run(JavacProcessingEnvironment.java:1035)
  	at com.sun.tools.javac.processing.JavacProcessingEnvironment.doProcessing(JavacProcessingEnvironment.java:1176)
  	at com.sun.tools.javac.main.JavaCompiler.processAnnotations(JavaCompiler.java:1170)
  	at com.sun.tools.javac.main.JavaCompiler.compile(JavaCompiler.java:856)
  	at com.sun.tools.javac.main.Main.compile(Main.java:523)
  	at com.sun.tools.javac.api.JavacTaskImpl.doCall(JavacTaskImpl.java:129)
  	at com.sun.tools.javac.api.JavacTaskImpl.call(JavacTaskImpl.java:138)
  	at io.github.future0923.debug.tools.server.compiler.DynamicCompiler.build(DynamicCompiler.java:116)
  	at io.github.future0923.debug.tools.server.compiler.DynamicCompiler.buildByteCodes(DynamicCompiler.java:101)
  	at io.github.future0923.debug.tools.server.scoket.handler.RemoteCompilerHotDeployRequestHandler.getByteCodes(RemoteCompilerHotDeployRequestHandler.java:25)
  	at io.github.future0923.debug.tools.server.scoket.handler.RemoteCompilerHotDeployRequestHandler.getByteCodes(RemoteCompilerHotDeployRequestHandler.java:12)
  	at io.github.future0923.debug.tools.server.scoket.handler.AbstractHotDeployRequestHandler.handle(AbstractHotDeployRequestHandler.java:44)
  	at io.github.future0923.debug.tools.common.handler.PacketHandleService.handle(PacketHandleService.java:30)
  	at io.github.future0923.debug.tools.server.thread.ClientHandleThread.run(ClientHandleThread.java:64)
Caused by: java.lang.ClassNotFoundException: com.sun.tools.javac.processing.JavacProcessingEnvironment
  	at java.lang.ClassLoader.findClass(ClassLoader.java:530)
  	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
  	at lombok.launch.ShadowClassLoader.loadClass(ShadowClassLoader.java:555)
  	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
  	... 23 more , line: 16 , 
```

A: 这个错误一般是在运行远程编译时缺少 `tools.jar` 模块。在编译过程中，比如使用了 Lombok、MapStruct 或其他注解处理器（annotation processor）时，这些库有时会试图访问 JavacProcessingEnvironment 等类。但这些类只在 JDK 的 tools.jar 中才有。

执行 `java -XshowSettings:properties -version` 命令查看 `java.home` 的值。

```text
➜  target git:(main) ✗ java -XshowSettings:properties -version

Property settings:
    awt.toolkit = sun.lwawt.macosx.LWCToolkit
    file.encoding = UTF-8
    file.encoding.pkg = sun.io
    file.separator = /
    ftp.nonProxyHosts = 192.168.0.0/16|*.192.168.0.0/16|10.0.0.0/8|*.10.0.0.0/8|172.16.0.0/12|*.172.16.0.0/12|127.0.0.1|localhost|*.localhost|local|*.local|timestamp.apple.com|*.timestamp.apple.com|sequoia.apple.com|*.sequoia.apple.com|seed-sequoia.siri.apple.com|*.seed-sequoia.siri.apple.com
    gopherProxySet = false
    http.nonProxyHosts = 192.168.0.0/16|*.192.168.0.0/16|10.0.0.0/8|*.10.0.0.0/8|172.16.0.0/12|*.172.16.0.0/12|127.0.0.1|localhost|*.localhost|local|*.local|timestamp.apple.com|*.timestamp.apple.com|sequoia.apple.com|*.sequoia.apple.com|seed-sequoia.siri.apple.com|*.seed-sequoia.siri.apple.com
    http.proxyHost = 127.0.0.1
    http.proxyPort = 7890
    https.proxyHost = 127.0.0.1
    https.proxyPort = 7890
    java.awt.graphicsenv = sun.awt.CGraphicsEnvironment
    java.awt.printerjob = sun.lwawt.macosx.CPrinterJob
    java.class.path = .
    java.class.version = 52.0
    java.endorsed.dirs = /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/endorsed
    java.ext.dirs = /Users/weilai/Library/Java/Extensions
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/ext
        /Library/Java/Extensions
        /Network/Library/Java/Extensions
        /System/Library/Java/Extensions
        /usr/lib/java
    java.home = /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre  // [!code focus]
    java.io.tmpdir = /var/folders/34/d7xb0l054cgb76_y_kx9r4f40000gn/T/
    java.library.path = /Users/weilai/Library/Java/Extensions
        /Library/Java/Extensions
        /Network/Library/Java/Extensions
        /System/Library/Java/Extensions
        /usr/lib/java
        .
    java.runtime.name = Java(TM) SE Runtime Environment
    java.runtime.version = 1.8.0_181-b13
    java.specification.name = Java Platform API Specification
    java.specification.vendor = Oracle Corporation
    java.specification.version = 1.8
    java.vendor = Oracle Corporation
    java.vendor.url = http://java.oracle.com/
    java.vendor.url.bug = http://bugreport.sun.com/bugreport/
    java.version = 1.8.0_181
    java.vm.info = mixed mode
    java.vm.name = Java HotSpot(TM) 64-Bit Server VM
    java.vm.specification.name = Java Virtual Machine Specification
    java.vm.specification.vendor = Oracle Corporation
    java.vm.specification.version = 1.8
    java.vm.vendor = Oracle Corporation
    java.vm.version = 25.181-b13
    line.separator = \n
    os.arch = x86_64
    os.name = Mac OS X
    os.version = 10.16
    path.separator = :
    socksNonProxyHosts = 192.168.0.0/16|*.192.168.0.0/16|10.0.0.0/8|*.10.0.0.0/8|172.16.0.0/12|*.172.16.0.0/12|127.0.0.1|localhost|*.localhost|local|*.local|timestamp.apple.com|*.timestamp.apple.com|sequoia.apple.com|*.sequoia.apple.com|seed-sequoia.siri.apple.com|*.seed-sequoia.siri.apple.com
    socksProxyHost = 127.0.0.1
    socksProxyPort = 7890
    sun.arch.data.model = 64
    sun.boot.class.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/resources.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/rt.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/sunrsasign.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/jsse.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/jce.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/charsets.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib/jfr.jar
        /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/classes
    sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/lib
    sun.cpu.endian = little
    sun.cpu.isalist =
    sun.io.unicode.encoding = UnicodeBig
    sun.java.launcher = SUN_STANDARD
    sun.jnu.encoding = UTF-8
    sun.management.compiler = HotSpot 64-Bit Tiered Compilers
    sun.os.patch.level = unknown
    user.country = CN
    user.dir = /Users/weilai/Documents/debug-tools/debug-tools-test/debug-tools-test-spring-boot-mybatis/target
    user.home = /Users/weilai
    user.language = zh
    user.name = weilai
    user.timezone =

java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
```