@echo off
rem -------------------------------------------------------------------------
rem JBoss JVM Launcher
rem -------------------------------------------------------------------------

rem $Id: shutdown.bat,v 1.7 2003/04/07 22:09:14 kimptoc Exp $

if not "%ECHO%" == ""  echo %ECHO%
if "%OS%" == "Windows_NT"  setlocal

set MAIN_JAR_NAME=shutdown.jar
set MAIN_CLASS=org.jboss.Shutdown

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

rem Read all command line arguments

set ARGS=
:loop
if [%1] == [] goto end
        set ARGS=%ARGS% %1
        shift
        goto loop
:end

rem Find MAIN_JAR, or we can't continue

set MAIN_JAR=%DIRNAME%\%MAIN_JAR_NAME%
if exist "%MAIN_JAR%" goto FOUND_MAIN_JAR
echo Could not locate %MAIN_JAR%. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_MAIN_JAR

if not "%JAVA_HOME%" == "" goto HAVE_JAVA_HOME

set JAVA=java

echo JAVA_HOME is not set.  Unexpected results may occur.
echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
goto SKIP_SET_JAVA_HOME

:HAVE_JAVA_HOME

set JAVA=%JAVA_HOME%\bin\java

:SKIP_SET_JAVA_HOME

set JBOSS_CLASSPATH=%JBOSS_CLASSPATH%;%MAIN_JAR%;../client/jnet.jar;../lib/jboss-system.jar

rem Setup JBoss sepecific properties
set JAVA_OPTS=%JAVA_OPTS% -Djboss.boot.loader.name=%PROGNAME%

%JAVA% %JAVA_OPTS% -classpath "%JBOSS_CLASSPATH%" %MAIN_CLASS% %ARGS%

:END
if "%NOPAUSE%" == "" pause

:END_NO_PAUSE
