# Servidor Web Multi-hilo HTTP/1.0

## Descripción

Este es un servidor web multi-hilo implementado en Java puro que cumple con el protocolo HTTP/1.0. El servidor es capaz de:

- Escuchar conexiones TCP en un puerto configurable (predeterminado: 8080)
- Crear un hilo independiente para cada solicitud HTTP recibida
- Interpretar solicitudes HTTP/1.0 usando el método GET
- Servir archivos HTML e imágenes (JPG, GIF, PNG)
- Implementar paginación y ordenamiento de datos
- Responder con códigos HTTP apropiados (200, 404)
- Cerrar sockets correctamente sin afectar la concurrencia

## Estructura del Proyecto

```
entrega servidor web/
├── src/
│   ├── WebServer.java              # Clase principal del servidor
│   ├── ClientHandler.java          # Manejador de solicitudes por hilo
│   ├── Course.java                 # Modelo de datos
│   ├── CourseManager.java          # Gestor de datos con paginación
│   └── HTTPResponse.java           # Constructor de respuestas HTTP
├── public/
│   ├── index.html                  # Página principal con enlaces
│   ├── courses.html                # Página de cursos con paginación
│   └── images/                     # Carpeta para imágenes (JPG, GIF, PNG)
├── requirements.txt
└── README.md
```

## Cómo Ejecutar

### Compilación

```bash
cd "c:\Users\Sebastian Romero\Documents\Universidad Icesi\Semestre VI\Compunet II\entrega servidor web"
javac -d bin src/*.java
```
(en mi pc)

### Ejecución

```bash
java -cp bin WebServer
```

El servidor escuchará en `http://localhost:8080`

## Características Implementadas

### 1. Multi-hilo
Cada solicitud HTTP se maneja en un hilo independiente, permitiendo que el servidor atienda múltiples clientes simultáneamente.

### 2. Paginación
- Página por defecto: 0 (primeros 3 registros)
- Tamaño de página: 3 registros
- Acceso via URL: `http://localhost:8080/courses.html?page=0&size=3`

### 3. Ordenamiento
Los cursos se pueden ordenar por:
- Nombre (ascendente/descendente)
- Créditos (ascendente/descendente)
- Profesor

### 4. Servicio de Recursos
- **HTML**: Content-Type: text/html
- **Imágenes JPG**: Content-Type: image/jpeg
- **Imágenes GIF**: Content-Type: image/gif
- **Imágenes PNG**: Content-Type: image/png

### 5. Manejo de Errores
- Archivos no encontrados: Respuesta 404
- Sockets cerrados correctamente
- Logs en consola para debugging

## Datos de Prueba

El servidor incluye datos de prueba con 6 cursos:
- Anatomía Humana (5 créditos) - Juan Pérez
- Fisiología (5 créditos) - Juan Pérez
- Derecho Penal (4 créditos) - María López
- Derecho Civil (4 créditos) - María López
- Historia del Arte (3 créditos) - Carlos García
- Introducción a la Programación (3 créditos) - Carlos García


## Prueba de Funcionamiento

1. Inicia el servidor
2. Abre tu navegador en `http://localhost:8080/index.html`
3. Haz clic en los enlaces para navegar:
   - Ver todos los cursos (paginado)
   - Primera página de cursos
   - Cursos ordenados por nombre
   - Cursos ordenados por créditos
   - Página no encontrada (error 404)

## Logs en Consola

El servidor mueesta:
- Línea de solicitud HTTP recibida
- Headers HTTP recibidos
- Ruta solicitada
- Recurso encontrado/no encontrado
- Tiempo de procesamiento

Ejemplo:
```
[INFO] GET /index.html HTTP/1.0
[INFO] Host: localhost:8080
[INFO] Sirviendo archivo: public/index.html
[INFO] Archivo encontrado. Tamaño: 1234 bytes
```

## Notas de Implementación

- El servidor usa `ServerSocket` para escuchar conexiones
- Cada solicitud se procesa en un `Thread` independiente
- Las respuestas siguen el formato HTTP/1.0 estándar
- Los recursos se sirven desde la carpeta `public/`
- Los parámetros de consulta se analizan desde la URL (query string)
