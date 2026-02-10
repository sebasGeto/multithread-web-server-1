import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Constructor de respuestas HTTP/1.0
 */
public class HTTPResponse {
    private int statusCode;
    private String contentType;
    private byte[] bodyBytes;
    private String bodyString;

    public HTTPResponse(int statusCode, String contentType) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.bodyBytes = new byte[0];
    }

    /**
     * Establece el cuerpo de la respuesta como String
     */
    public void setBody(String body) {
        this.bodyString = body;
        try {
            this.bodyBytes = body.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.bodyBytes = body.getBytes();
        }
    }

    /**
     * Establece el cuerpo de la respuesta como bytes (para imágenes)
     */
    public void setBodyBytes(byte[] bytes) {
        this.bodyBytes = bytes;
    }

    /**
     * Convierte la respuesta a bytes para enviarla por la red
     */
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String statusLine = getStatusLine();
        baos.write(statusLine.getBytes("UTF-8"));
        baos.write("\r\n".getBytes("UTF-8"));

        String headers = getHeaders();
        baos.write(headers.getBytes("UTF-8"));

        baos.write("\r\n".getBytes("UTF-8"));

        if (bodyBytes.length > 0) {
            baos.write(bodyBytes);
        }

        return baos.toByteArray();
    }

    /**
     * Obtiene la línea de estado HTTP
     */
    private String getStatusLine() {
        String reasonPhrase = getReasonPhrase();
        return "HTTP/1.0 " + statusCode + " " + reasonPhrase;
    }

    /**
     * Obtiene la frase de estado
     */
    private String getReasonPhrase() {
        switch (statusCode) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unknown";
        }
    }

    /**
     * Obtiene los headers HTTP
     */
    private String getHeaders() {
        StringBuilder headers = new StringBuilder();

        headers.append("Content-Type: ").append(contentType).append("\r\n");

        headers.append("Content-Length: ").append(bodyBytes.length).append("\r\n");

        headers.append("Date: ").append(getCurrentDate()).append("\r\n");

        headers.append("Server: CompunetWebServer/1.0\r\n");

        headers.append("Connection: close\r\n");

        return headers.toString();
    }

    /**
     * Obtiene la fecha actual en formato HTTP
     */
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }
}
