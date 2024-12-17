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
