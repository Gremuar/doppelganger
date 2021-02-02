@echo off

SET jPath="X:\Tests\Java\jdk-11\bin\"
IF EXIST %jPath%java %jPath%java -jar doppelganger-1.0.0.jar --spring.profiles.active=prod
pause