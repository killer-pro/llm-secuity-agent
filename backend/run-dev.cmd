@echo off
REM Development startup script for Security AI Platform
REM This script starts the backend with local development configuration

echo.
echo ============================================
echo Security AI Platform - Backend (Dev)
echo ============================================
echo.
echo Starting Spring Boot with local configuration...
echo API Provider: Gemini (Real API Key Configured)
echo.

REM Start Spring Boot with local profile
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

pause
