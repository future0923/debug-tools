@REM
@REM Copyright (C) 2024-2025 the original author or authors.
@REM
@REM This program is free software: you can redistribute it and/or modify
@REM it under the terms of the GNU General Public License as published by
@REM the Free Software Foundation, either version 3 of the License, or
@REM (at your option) any later version.
@REM
@REM This program is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM GNU General Public License for more details.
@REM
@REM You should have received a copy of the GNU General Public License
@REM along with this program.  If not, see <https://www.gnu.org/licenses/>.
@REM

@echo off
set COMPILE_JAVA_HOME=%JAVA_HOME%
if "%COMPILE_JAVA_HOME%"=="" (
    echo ERROR: _JAVA_HOME is not set. Exiting...
    exit /b 1
)
x86_64-w64-mingw32-g++ ..\src\main\native\src\io_github_future0923_debug_tools_vm_VmTool.cpp -shared -o ..\..\debug-tools-server\src\main\resources\lib\libJniLibrary-x64.dll -I../src/main/native/src/ -I%COMPILE_JAVA_HOME%\include -I%COMPILE_JAVA_HOME%\include\win32 -static