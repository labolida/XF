@ECHO OFF
CLS
ECHO ************************************
ECHO ITARVI BBVA [TTF]
ECHO Prepare files to deploy
ECHO ************************************


REM # set d=C:\area6\java\dev\bbva\ttf.repository\download\

set d=A:\TTF\Mantis\0000\

C:\area6\java\bin\JDK.1.6.32bits\bin\java.exe -cp C:\area6\java\dev\desktop\LMLJDaemon02\ DaemonUnzipSql %d%


pause