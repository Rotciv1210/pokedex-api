@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM Maven Wrapper startup batch script, version 3.2.0
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0") ELSE (SET "BASE_DIR=%__MVNW_ARG0_NAME__%")

@SET MAVEN_PROJECTBASEDIR=%BASE_DIR%
@SET WRAPPER_DIR=%BASE_DIR%.mvn\wrapper
@SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
@SET WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties

@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%WRAPPER_PROPERTIES%") DO (
    @IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@IF NOT EXIST "%WRAPPER_JAR%" (
    @ECHO Downloading Maven Wrapper...
    @IF NOT EXIST "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
    @SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
    @powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%WRAPPER_JAR%'"
)

@IF EXIST "%JAVA_HOME%\bin\java.exe" (
    SET JAVA_EXEC=%JAVA_HOME%\bin\java.exe
) ELSE (
    SET JAVA_EXEC=java
)

@"%JAVA_EXEC%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %* --no-transfer-progress
