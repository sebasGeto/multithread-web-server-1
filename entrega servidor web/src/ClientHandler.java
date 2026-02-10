import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador de solicitudes HTTP en un hilo independiente
 * Procesa solicitudes GET HTTP/1.0 y sirve recursos
 */
public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private OutputStream out;
    private String baseDir = "public";

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = clientSocket.getOutputStream();

            String requestLine = in.readLine();
            if (requestLine == null) {
                closeConnection();
                return;
            }

            System.out.println("[INFO] " + requestLine);

            String[] parts = requestLine.split(" ");
            if (parts.length < 3) {
                send404();
                closeConnection();
                return;
            }

            String method = parts[0];
            String path = parts[1];

            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                System.out.println("[INFO] " + headerLine);
            }

            if (!method.equals("GET")) {
                send404();
                closeConnection();
                return;
            }

            handleRequest(path);

        } catch (IOException e) {
            System.err.println("[ERROR] Error en ClientHandler: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * Maneja diferentes tipos de solicitudes
     */
    private void handleRequest(String path) throws IOException {
        Map<String, String> params = parseQueryString(path);
        
        String cleanPath = path;
        if (cleanPath.contains("?")) {
            cleanPath = cleanPath.substring(0, cleanPath.indexOf("?"));
        }
        
        if (cleanPath.equals("/")) {
            cleanPath = "/index.html";
        }

        System.out.println("[INFO] Ruta solicitada: " + cleanPath);

        if (cleanPath.equals("/courses")) {
            servePaginatedCourses(params);
        } else if (cleanPath.equals("/courses.html")) {
            servePaginatedCourses(params);
        } else {
            serveFile(cleanPath);
        }
    }

    /**
     * Sirve la página de cursos con paginación
     */
    private void servePaginatedCourses(Map<String, String> params) throws IOException {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "3"));
        String sort = params.getOrDefault("sort", "name");
        String direction = params.getOrDefault("direction", "asc");

        CourseManager manager = new CourseManager();
        Page<Course> coursePage = manager.getPaginatedCourses(page, size, sort, direction);

        String html = generateCoursesHTML(coursePage, page, size, sort, direction);
        HTTPResponse response = new HTTPResponse(200, "text/html");
        response.setBody(html);
        sendResponse(response);
    }

    /**
     * Genera HTML para la página de cursos
     */
    private String generateCoursesHTML(Page<Course> coursePage, int page, int size, String sort, String direction) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"es\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Cursos - Servidor Web</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append("        .container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; }\n");
        html.append("        h1 { color: #333; text-align: center; }\n");
        html.append("        .course { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; background: #f9f9f9; }\n");
        html.append("        .course h3 { margin: 0 0 10px 0; color: #0066cc; }\n");
        html.append("        .course p { margin: 5px 0; }\n");
        html.append("        .pagination { text-align: center; margin: 20px 0; }\n");
        html.append("        .pagination a, .pagination span { padding: 8px 12px; margin: 0 4px; border: 1px solid #ddd; text-decoration: none; background: #f0f0f0; }\n");
        html.append("        .pagination a:hover { background: #0066cc; color: white; }\n");
        html.append("        .pagination .current { background: #0066cc; color: white; }\n");
        html.append("        .controls { margin: 20px 0; text-align: center; }\n");
        html.append("        .controls a { display: inline-block; padding: 10px 20px; margin: 5px; background: #0066cc; color: white; text-decoration: none; border-radius: 5px; }\n");
        html.append("        .controls a:hover { background: #0052a3; }\n");
        html.append("        .info { text-align: center; color: #666; margin: 10px 0; }\n");
        html.append("        .back-link { margin: 20px 0; }\n");
        html.append("        .back-link a { color: #0066cc; text-decoration: none; }\n");
        html.append("        .back-link a:hover { text-decoration: underline; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>Lista de Cursos</h1>\n");

        html.append("        <div class=\"info\">\n");
        html.append("            Página ").append(page + 1).append(" de ").append(coursePage.getTotalPages()).append(" | ");
        html.append("Total de cursos: ").append(coursePage.getTotalElements()).append("\n");
        html.append("        </div>\n");

        html.append("        <div class=\"controls\">\n");
        html.append("            <a href=\"/courses.html?page=0&size=").append(size).append("&sort=name&direction=asc\">Ordenar por Nombre ↑</a>\n");
        html.append("            <a href=\"/courses.html?page=0&size=").append(size).append("&sort=name&direction=desc\">Ordenar por Nombre ↓</a>\n");
        html.append("            <a href=\"/courses.html?page=0&size=").append(size).append("&sort=credits&direction=asc\">Ordenar por Créditos ↑</a>\n");
        html.append("            <a href=\"/courses.html?page=0&size=").append(size).append("&sort=credits&direction=desc\">Ordenar por Créditos ↓</a>\n");
        html.append("        </div>\n");

        for (Course course : coursePage.getContent()) {
            String courseImage = getCourseImage(course.getId());
            html.append("        <div class=\"course\">\n");
            if (!courseImage.isEmpty()) {
                html.append("            <img src=\"").append(courseImage).append("\" alt=\"").append(course.getName()).append("\" style=\"width: 100%; height: 200px; object-fit: cover; border-radius: 5px; margin-bottom: 15px;\">\n");
            }
            html.append("            <h3>").append(course.getName()).append("</h3>\n");
            html.append("            <p><strong>Profesor:</strong> ").append(course.getProfessor()).append("</p>\n");
            html.append("            <p><strong>Créditos:</strong> ").append(course.getCredits()).append("</p>\n");
            html.append("        </div>\n");
        }

        // Paginación
        html.append("        <div class=\"pagination\">\n");
        if (page > 0) {
            html.append("            <a href=\"/courses.html?page=").append(page - 1).append("&size=").append(size).append("&sort=").append(sort).append("&direction=").append(direction).append("\">← Anterior</a>\n");
        }
        for (int i = 0; i < coursePage.getTotalPages(); i++) {
            if (i == page) {
                html.append("            <span class=\"current\">").append(i + 1).append("</span>\n");
            } else {
                html.append("            <a href=\"/courses.html?page=").append(i).append("&size=").append(size).append("&sort=").append(sort).append("&direction=").append(direction).append("\">").append(i + 1).append("</a>\n");
            }
        }
        if (page < coursePage.getTotalPages() - 1) {
            html.append("            <a href=\"/courses.html?page=").append(page + 1).append("&size=").append(size).append("&sort=").append(sort).append("&direction=").append(direction).append("\">Siguiente →</a>\n");
        }
        html.append("        </div>\n");

        // Enlaces 
        html.append("        <div class=\"back-link\">\n");
        html.append("            <a href=\"/index.html\">← Volver al inicio</a>\n");
        html.append("        </div>\n");

        html.append("    </div>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    /**
     * Sirve un archivo estático desde la carpeta public
     */
    private void serveFile(String path) throws IOException {
        if (path.contains("..")) {
            send404();
            return;
        }

        Path filePath = Paths.get(baseDir, path);
        File file = filePath.toFile();

        System.out.println("[INFO] Sirviendo archivo: " + filePath);

        if (file.exists() && file.isFile()) {
            System.out.println("[INFO] Archivo encontrado. Tamaño: " + file.length() + " bytes");

            String mimeType = getMimeType(path);
            HTTPResponse response = new HTTPResponse(200, mimeType);

            byte[] fileContent = Files.readAllBytes(filePath);
            response.setBodyBytes(fileContent);

            sendResponse(response);
        } else {
            System.out.println("[INFO] Archivo no encontrado: " + path);
            send404();
        }
    }

    /**
     * Determina el tipo MIME según la extensión del archivo
     */
    private String getMimeType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "text/plain";
        }
    }

    /**
     * Parsea los parámetros de query string
     */
    private Map<String, String> parseQueryString(String url) {
        Map<String, String> params = new HashMap<>();
        
        if (!url.contains("?")) {
            return params;
        }

        String queryString = url.substring(url.indexOf("?") + 1);
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    params.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    System.err.println("[ERROR] Error al decodificar parámetro: " + pair);
                }
            }
        }

        return params;
    }

    /**
     * Envía una respuesta HTTP
     */
    private void sendResponse(HTTPResponse response) throws IOException {
        out.write(response.getBytes());
        out.flush();
    }

    /**
     * Envía una respuesta 404
     */
    private void send404() throws IOException {
        String html = "<!DOCTYPE html>\n<html>\n<head><title>404 Not Found</title></head>\n" +
                      "<body><h1>404 - Recurso No Encontrado</h1><p>El archivo solicitado no existe.</p>" +
                      "<a href=\"/index.html\">Volver al inicio</a></body>\n</html>";
        HTTPResponse response = new HTTPResponse(404, "text/html");
        response.setBody(html);
        sendResponse(response);
    }

    /**
     * Obtiene la imagen asociada a un curso basado en su ID
     */
    private String getCourseImage(int courseId) {
        switch (courseId) {
            case 1:
            case 2:
                return "/images/curso1.jpg";
            case 3:
            case 4:
                return "/images/curso2.jpg";
            case 5:
            case 6:
                return "/images/curso3.jpg";
            default:
                return "/images/mi-imagen.jpg";
        }
    }

    /**
     * Cierra la conexión de forma segura
     */
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error al cerrar conexión: " + e.getMessage());
        }
    }
}
