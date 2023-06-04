@ECHO OFF

where java >nul 2>nul
if %errorlevel%==1 (
    @echo ERROR: Java could not be found in your PATH!
    goto end
)

java -jar ./SnakeBattleArenaServer.jar -Xmx1G

:end
pause
