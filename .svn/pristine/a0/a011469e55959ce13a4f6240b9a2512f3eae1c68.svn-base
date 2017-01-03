@echo OFF

@set flrshell=flora_shell
if [%~n0] == [runergo]  set flrshell=ergo_shell

REM     set JAVA_EXECUTABLE manually, if this automatic method fails
REM for /f "usebackq tokens=*" %%i in (`where java.exe`) do set JAVA_EXECUTABLE=%%i
REM     @echo ** Using Java executable in %JAVA_EXECUTABLE%
REM     @echo ** Please make sure that Java has version 1.8 or later

@set thisdir=%0\..\
@call %thisdir%.flora_paths.bat

set STARTUPOPTIONS=
set PROFILEFLAG=
set PROFILING=
if [%1] == [--devel] (
    @set PROFILEFLAG="-p"
    @set PROFILING="xsb_profiling:profile_unindexed_calls(on),"
    shift
) else set STARTUPOPTIONS=--noprompt --quietload --nofeedback --nobanner

REM @set PROLOGOPTIONS="-m 2000000 -c 50000"
REM @set PROLOGOPTIONS="-p -m 2000000 -c 50000"

@%PROLOG% %STARTUPOPTIONS% %PROLOGOPTIONS% %PROFILEFLAG% -e "%PROFILING% asserta(library_directory(%FLORADIR%)). [flora2]. %flrshell%." %1 %2 %3 %4 %5 %6 %7
