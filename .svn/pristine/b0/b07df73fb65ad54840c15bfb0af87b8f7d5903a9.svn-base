@echo off

REM  Generate the files that contain the Prolog & Flora installation directories

@set OBJEXT = .xwam
@set PROLOGEXT = .P

if [%1] == [] goto florausage

if [%2] == [] (if not exist genincludes\flrtable.flh goto floranotcompilederror)

@echo.
@set PROLOG=%1
call %PROLOG% -e "halt." > null 2>&1 || goto xsbinstallerror
call %PROLOG% -e "[flrdepstest]. halt." > null 2>&1 || goto xsbsourceserror
del null

REM This for-loop causes recompilation of all .flr files by cleaning out the
REM .xsb\flora-* directories
for /D %%i in (%USERPROFILE%\.xsb\"flora-*") do if exist "%%i\*%OBJEXT%" del /Q "%%i\*%OBJEXT%" "%%i\*%PROLOGEXT%"
for /D %%i in (%USERPROFILE%\.xsb\"ergo-*") do if exist "%%i\*%OBJEXT%" (del /Q "%%i\*%OBJEXT%" "%%i\*%PROLOGEXT%"
)

call %PROLOG% -e "[flrconfig]. halt." || goto floraconfigerror

if [%2] == [] goto success

goto end

:success
@echo.
@echo.
@echo +++++ All is well: you can now run Flora-2 using the script
@echo +++++    runflora.bat
@echo.
goto end

:xsbinstallerror
@echo.
@echo.
@echo +++++ Engine %PROLOG% has failed to start:
@echo +++++    XSB has not been configured properly at that location
@echo.
goto end

:xsbsourceserror
@echo.
@echo.
@echo +++++ XSB sources do not seem to have been installed
@echo.
goto end

:floranotcompilederror
@echo.
@echo.
@echo +++++ Flora-2 must first be compiled with the makeflora.bat command
@echo.
goto end

:floraconfigerror
@echo.
@echo.
@echo +++++ Failed to configure Flora-2
@echo +++++ Please report to flora-users@lists.sf.net and include the log
@echo.
goto end

:florausage
@echo.
@echo.
@echo +++++ Usage:
@echo +++++    floraconfig path-to-\XSB\bin\xsb.bat    (32 bit installations)
@echo +++++    floraconfig path-to-\XSB\bin\xsb64.bat  (64 bit installations)
@echo.
goto end

:end
set default_tabling=flrincludes\.flora_default_tabling
echo. > %default_tabling%

@echo.


REM Local Variables:
REM coding-system-for-write:  iso-2022-7bit-dos
REM End:
