#!/bin/bash
# Script de compilacion y ejecucion del servidor web

echo ""
echo "========================================"
echo "Compilando Servidor Web..."
echo "========================================"
echo ""

# Crear carpeta bin si no existe
mkdir -p bin

# Compilar archivos Java
javac -d bin src/*.java

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] La compilacion fallo."
    exit 1
fi

echo ""
echo "========================================"
echo "Compilacion completada exitosamente"
echo "========================================"
echo ""

# Iniciar el servidor
echo "Iniciando servidor en puerto 8080..."
echo "Accede a: http://localhost:8080/index.html"
echo ""
echo "Presiona Ctrl+C para detener el servidor."
echo ""

java -cp bin WebServer
