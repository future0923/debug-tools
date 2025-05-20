@REM
@REM Copyright 2024-2025 the original author or authors.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off
set COMPILE_JAVA_HOME=%JAVA_HOME%
if "%COMPILE_JAVA_HOME%"=="" (
    echo ERROR: _JAVA_HOME is not set. Exiting...
    exit /b 1
)
x86_64-w64-mingw32-g++ ..\src\main\native\src\io_github_future0923_debug_tools_vm_VmTool.cpp -shared -o ..\..\debug-tools-server\src\main\resources\lib\libJniLibrary-x64.dll -I../src/main/native/src/ -I%COMPILE_JAVA_HOME%\include -I%COMPILE_JAVA_HOME%\include\win32 -static