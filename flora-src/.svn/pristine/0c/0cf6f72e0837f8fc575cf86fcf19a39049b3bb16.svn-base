@echo off

REM  Call:
REM     makeflora [-S] [-P] [-c64] path-for-\XSB\bin\xsb64.bat
REM     makeflora clean

REM   makeflora -S path-for-\XSB\bin\xsb64.bat
REM       means: configure FLORA for subsumptive tabling
REM   makeflora -P path-for-\XSB\bin\xsb64.bat
REM       means: configure FLORA for passive (nonincremental) tabling
REM   makeflora -c64 path-for-\XSB\bin\xsb64.bat
REM       Also recompile relevant C files.
REM   .\cc - developer's option

REM  NOTE: DOS batch language is very brittle. For instance, replacing
REM        %1 with %ARG%, where ARG=%1 will not work if
REM        path-for-\XSB\bin\... has a file extension, e.g., \xsb\bin\wxsb.bat

if exist binary-distribution.txt goto binary

set thiscommand=makeflora
if [%1] == [makeergo]  (
   set thiscommand=makeergo
   shift
)
if [%1] == [makeergolite]  (
   set thiscommand=makeergolite
   shift
)

set tabling_method=/* default tabling */
if [%1] == [-S] (
   set tabling_method=#define FLORA_SUBSUMPTIVE_TABLING
   shift
)
if [%1] == [-P] (
   set tabling_method=#define FLORA_NONINCREMENTAL_TABLING
   shift
)
if [%1] == [-S] (
   set tabling_method=#define FLORA_SUBSUMPTIVE_TABLING
   shift
)

set target=ALL
if [%1] == [CORE] (
   set target=CORE
   shift
)
if [%1] == [core] (
   set target=CORE
   shift
)
if [%1] == [BASE] (
   set target=CORE
   shift
)
if [%1] == [base] (
   set target=CORE
   shift
)
if [%1] == [TOP] (
   set target=TOP
   shift
)
if [%1] == [top] (
   set target=TOP
   shift
)

if [%1] == [] goto usage

if        [%1] == [-c64]    (set prologcmd=%2
) else                    set prologcmd=%1

if [%prologcmd%] == [] goto usage


if [%1] == [clean] nmake /nologo /f NMakefile.mak clean
if [%1] == [clean] goto end

call floraconfig.bat %prologcmd% compiling || goto end
REM This sets %PROLOG%, %PROLOGDIR%, %FLORADIR%
call .flora_paths.bat

set default_tabling=flrincludes\.flora_default_tabling
echo %tabling_method%  >  %default_tabling%

cd cc
if exist *.dll   del *.dll
if exist *.lib   del *.lib
if exist *.def   del *.def
if exist *.exp   del *.exp
if exist *.obj   del *.obj
if exist *.a     del *.a
if exist *.o     del *.o
if exist *.xwam  del *.xwam

REM These compile DLLs
if [%1] == [-c64] (
   nmake /nologo /f NMakefile64.mak PROLOG=%PROLOG% PROLOGDIR=%PROLOGDIR%
   if [%thiscommand%] == [makeergo]  (
      cd json
      nmake /nologo /f NMakefile64.mak PROLOG=%PROLOG% PROLOGDIR=%PROLOGDIR%
      cd ..
      cd ergoact
      nmake /nologo /f NMakefile64.mak PROLOG=%PROLOG% PROLOGDIR=%PROLOGDIR%
      cd ..
      cd ergoflt
      nmake /nologo /f NMakefile64.mak PROLOG=%PROLOG% PROLOGDIR=%PROLOGDIR%
      cd ..
   )
   cd ..
   goto end
)

cd ..

if [%thiscommand%] == [makeflora]  (
   if exist ergoisms\ergo*.switch del ergoisms\ergo*.switch
)

REM The following commands touch files
cd flrincludes
copy /b flora_terms.flh +,,
copy /b flora_exceptions.flh +,,
cd ..
copy /b flrversion.P +,,
copy /b flora2.P +,,

REM Now compile the rest of the system
nmake /nologo /f NMakefile.mak %target% PROLOG=%PROLOG% PROLOGDIR=%PROLOGDIR%


goto end

:usage
@echo.
@echo +++++ Usage:
@echo +++++   %thiscommand% [-c64] path-to-\XSB\bin\xsb64
@echo.
goto end

:binary
@echo.
@echo +++++ This seems to be a binary distribution of FLORA-2.
@echo +++++ Please use floraconfig.bat to configure it:
@echo +++++   floraconfig path-for-\XSB\bin\xsb64.bat
@echo.
goto end

:end
@echo.

REM Local Variables:
REM coding-system-for-write:  iso-2022-7bit-dos
REM End:
