@ECHO OFF
CLS
ECHO ***********************************
ECHO ITARVI BBVA CIB TTF
ECHO Daemon: Waiting for DeploymentFiles
ECHO ***********************************


REM # set d=C:\area6\java\dev\bbva\ttf.repository\download\

set d=A:\TTF\Mantis\0000\

C:\area6\java\bin\JDK.1.6.32bits\bin\java.exe -cp C:\area6\java\bin\tomcat7\webapps\XF\daemon\;C:\area6\java\bin\tomcat7\webapps\XF\daemon\derbyclient.jar Daemon %d%


pause