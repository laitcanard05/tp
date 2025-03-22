@echo off
setlocal enableextensions
pushd %~dp0

cd ..
del /f /q data\finbro.txt
call gradlew clean shadowJar

cd build\libs
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

:: Get today's date in YYYY-MM-DD format
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd"') do set TODAY=%%i

:: Replace <DATE> with today's date in EXPECTED.TXT
powershell -Command "(Get-Content EXPECTED.TXT) -replace '<DATE>', '%TODAY%' | Set-Content EXPECTED-UNIX.TXT"

:: Compare output
FC ACTUAL.TXT EXPECTED-UNIX.TXT >NUL && echo Test passed! || echo Test failed!
