# [3.4.3](https://github.com/future0923/debug-tools/compare/v3.4.2...v3.4.3) (2025-04-01)

- Fix the bug that hot reload fails when modifying the Controller file when there is only one Controller file([#31](https://github.com/future0923/debug-tools/issues/31)).
- Fix the bug of JDK21's hot reload startup failure([#30](https://github.com/future0923/debug-tools/issues/30)).
- Fix the bug of WebFlux call failure([#29](https://github.com/future0923/debug-tools/issues/29)).

# [3.4.2](https://github.com/future0923/debug-tools/compare/v3.4.1...v3.4.2) (2025-03-17)

- Added a button to compile XML files to trigger hot reload([#21](https://github.com/future0923/debug-tools/issues/21)).
- Fix the bug that hot reload doesn't work after renaming the Controller([#23](https://github.com/future0923/debug-tools/issues/23)).
- Fix the bug that can't connect to remote applications([#27](https://github.com/future0923/debug-tools/issues/27)).

# [3.4.1](https://github.com/future0923/debug-tools/compare/v3.4.0...v3.4.1) (2025-02-27)

- Fix the bug of ConcurrentModificationException when reloading multiple files in MyBatis([#20](https://github.com/future0923/debug-tools/issues/20)).

# [3.4.0](https://github.com/future0923/debug-tools/compare/v3.3.0...v3.4.0) (2025-02-20)

- Hot reload supports static variable、static final variable、static code block in class([#10](https://github.com/future0923/debug-tools/issues/10)).
- Hot reload supports enum class([#10](https://github.com/future0923/debug-tools/issues/10))
- Fixed the bug where non-bean classes couldn't be called in the Spring environment([#16](https://github.com/future0923/debug-tools/issues/16)).

# [3.3.0](https://github.com/future0923/debug-tools/compare/v3.2.0...v3.3.0) (2025-02-11)

- Hot reload supports MyBatis Plus to add Entity/Mapper files.
- Hot reload supports MyBatis Plus to add/modify xml files.
- Hot reload supports MyBatis to add/modify Mapper/Xml files.
- Optimize Execute Last([#12](https://github.com/future0923/debug-tools/issues/12)).
- Fix the exception bug when executing the Maven command to print SQL([#7](https://github.com/future0923/debug-tools/issues/7)).

# [3.2.0](https://github.com/future0923/debug-tools/compare/v3.1.2...v3.2.0) (2025-01-09)

- Hot reload, the written code can take effect without restarting the application.

# [3.1.2](https://github.com/future0923/debug-tools/compare/v3.1.1...v3.1.2) (2024-12-30)

- Fixed the bug of removing ContextPath ([#3](https://github.com/future0923/debug-tools/issues/3)).
- Fixed the bug that multiple projects share one attach button ([#4](https://github.com/future0923/debug-tools/issues/4)).

# [3.1.1](https://github.com/future0923/debug-tools/compare/v3.1.0...v3.1.1) (2024-12-18)

- Search http url can remove ContextPath information. 
- Fixed the bug that domain name cannot be matched without http(s).

# [3.1.0](https://github.com/future0923/debug-tools/compare/v3.0.1...v3.1.0) (2024-12-17)

- Search http url to jump directly to the corresponding method definition.
- Fixed bug where the default ClassLoader could not be obtained when remotely executing Groovy.
- Fixed bug that DebugToolsClassLoder repeatedly loads AppClassLoader to load the base package, causing DefaultClassLoader to get an exception.
- Optimize log printing ([#2](https://github.com/future0923/debug-tools/issues/2))

# [3.0.1](https://github.com/future0923/debug-tools/compare/v3.0.0...v3.0.1) (2024-12-12)

- Support intellij idea 2024.3 version.
- Fixed bug where the default ClassLoader could not be obtained when remotely executing Groovy.

# 3.0.0 (2024-11-01)

### Features

- You can choose classloader.
- Remote application methods can be invoked.
