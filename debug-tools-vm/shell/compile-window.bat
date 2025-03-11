@echo off
set COMPILE_JAVA_HOME=%JAVA_HOME%
if "%COMPILE_JAVA_HOME%"=="" (
    echo ERROR: _JAVA_HOME is not set. Exiting...
    exit /b 1
)
x86_64-w64-mingw32-g++ ..\src\main\native\src\io_github_future0923_debug_tools_vm_VmTool.cpp -shared -o ..\..\debug-tools-server\src\main\resources\lib\libJniLibrary-x64.dll -I../src/main/native/src/ -I%COMPILE_JAVA_HOME%\include -I%COMPILE_JAVA_HOME%\include\win32 -static