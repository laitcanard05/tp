@echo off
setlocal enabledelayedexpansion
pushd %~dp0

cd ..
del /f /q data\finbro.txt
call gradlew clean shadowJar

REM Find the JAR file
for /f %%a in ('dir /b /o-d build\libs\*.jar') do (
    set jarloc=build\libs\%%a
    goto foundjar
)
:foundjar

cd text-ui-test

REM Get today's date in YYYY-MM-DD format
for /f %%i in ('powershell -command "Get-Date -Format yyyy-MM-dd"') do set TODAY=%%i

REM Replace <DATE> with today's date
powershell -Command "(Get-Content EXPECTED.TXT) -replace '<DATE>', '%TODAY%' | Set-Content EXPECTED-WINDOWS.TXT"

REM Run the test
java -jar ..\%jarloc% < input.txt > ACTUAL.TXT

REM Compare the output
FC /W ACTUAL.TXT EXPECTED-WINDOWS.TXT >NUL
if %errorlevel% equ 0 (
    echo Test passed!
    exit /b 0
) else (
    echo Test failed!
    exit /b 1
)
