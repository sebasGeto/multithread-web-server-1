@echo off
REM Script de compilacion y ejecucion del servidor web
setlocal enabledelayedexpansion

echo.
echo ========================================
echo Compilando Servidor Web...
echo ========================================
echo.

REM Crear carpeta bin si no existe
if not exist bin mkdir bin

REM Compilar archivos Java
javac -d bin src\*.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] La compilacion fallo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Compilacion completada exitosamente
echo ========================================
echo.

REM Iniciar el servidor
echo Iniciando servidor en puerto 8080...
echo Accede a: http://localhost:8080/index.html
echo.
echo Presiona Ctrl+C para detener el servidor.
echo.

java -cp bin WebServer

pause
