@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal


rem java -DINFO_ALL -jar run.jar

set CLASSPATH=%CLASSPATH%;run.jar

if not "%TOMCAT_HOME%" == "" goto gotTomcatHome
goto noTomcatHome

:gotTomcatHome
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\jasper.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\webserver.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\xml.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\jaxp.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\parser.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\servlet.jar

REM Add the tools.jar file so that Tomcat can find the 
REM Java compiler.

set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

:noTomcatHome

REM if "%SPYDERMQ_HOME%" == "" goto startJBoss
REM
REM :gotSpyderMQHome
REM set CLASSPATH=%CLASSPATH%;%SPYDERMQ_HOME%\lib
REM set CLASSPATH=%CLASSPATH%;%SPYDERMQ_HOME%\lib\spydermq.jar
REM set CLASSPATH=%CLASSPATH%;%SPYDERMQ_HOME%\lib\jms.jar
REM set CLASSPATH=%CLASSPATH%;%SPYDERMQ_HOME%\lib\jnpserver.jar

:startJBoss
REM Add all login modules for JAAS-based security
REM and all libraries that are used by them here
set CLASSPATH=%CLASSPATH%;..\lib\jdbc2_0-stdext.jar;..\lib\jboss-jaas.jar


java -classpath "%CLASSPATH%" -Dtomcat.home=%TOMCAT_HOME% org.jboss.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

pause
