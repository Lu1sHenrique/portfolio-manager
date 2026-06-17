@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MVNW_BATCH_ECHO is 'on'
@echo off
@REM set title of command window
title %0
@REM enable echoing by setting MVNW_BATCH_ECHO to 'on'
@if "%MVNW_BATCH_ECHO%"=="on" echo %MVNW_BATCH_ECHO%

@REM Set local scope for the variables with windows NT shell
setlocal

set ERROR_CODE=0

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%"=="" goto OkJHome
for %%i in (java.exe) do set "JAVACMD=%%~$PATH:i"
goto checkJCmd

:OkJHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"

:checkJCmd
if exist "%JAVACMD%" goto chkMHome

echo The JAVA_HOME environment variable is not defined correctly, >&2
echo this environment variable is needed to run this program. >&2
goto error

:chkMHome
set "MAVEN_PROJECTBASEDIR=%~dp0"
@REM This code handles the edge case where the project directory path has a trailing backslash
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"

@REM ==== DOWNLOAD MAVEN IF NEEDED ====
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

@REM Determine Maven version from maven-wrapper.properties
set MAVEN_VERSION=3.9.6
set "DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

@REM Default Maven user home
if "%MAVEN_USER_HOME%"=="" set "MAVEN_USER_HOME=%USERPROFILE%\.m2"

@REM Compute the hash of the URL for the wrapper directory name
set "WRAPPER_DIST=%MAVEN_USER_HOME%\wrapper\dists\apache-maven-%MAVEN_VERSION%"

if exist "%WRAPPER_DIST%\bin\mvn.cmd" goto runMaven

@REM Create wrapper directory if it doesn't exist
if not exist "%WRAPPER_DIST%" mkdir "%WRAPPER_DIST%"

@REM Download Maven distribution
echo Downloading Apache Maven %MAVEN_VERSION%...
set "DOWNLOAD_FILE=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip"

@REM Try PowerShell first
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%DOWNLOAD_FILE%' }" 2>nul
if %ERRORLEVEL% neq 0 (
    @REM Try curl
    curl -fsSL -o "%DOWNLOAD_FILE%" "%DOWNLOAD_URL%" 2>nul
    if %ERRORLEVEL% neq 0 (
        echo Failed to download Maven. Please check your internet connection.
        goto error
    )
)

@REM Extract the zip file
echo Extracting Maven...
powershell -Command "& { Expand-Archive -Path '%DOWNLOAD_FILE%' -DestinationPath '%WRAPPER_DIST%\..' -Force }" 2>nul
if %ERRORLEVEL% neq 0 (
    echo Failed to extract Maven archive.
    goto error
)

@REM Clean up
del "%DOWNLOAD_FILE%" 2>nul

@REM Rename extracted folder if needed
if exist "%WRAPPER_DIST%\..\apache-maven-%MAVEN_VERSION%" (
    if not exist "%WRAPPER_DIST%" (
        move "%WRAPPER_DIST%\..\apache-maven-%MAVEN_VERSION%" "%WRAPPER_DIST%" >nul
    )
)

:runMaven
set "MAVEN_HOME=%WRAPPER_DIST%"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

@REM Check if mvn.cmd exists
if not exist "%MAVEN_CMD%" (
    echo Maven installation not found at %MAVEN_CMD%
    goto error
)

@REM Execute Maven
"%MAVEN_CMD%" %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%
