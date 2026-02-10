import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor Web Multi-hilo HTTP/1.0
 * Escucha conexiones TCP en puerto configurable y crea un hilo para cada solicitud
 */
public class WebServer {

    private int port;
    private ServerSocket serverSocket;

    public WebServer(int port) {
        this.port = port;
    }

    /**
     * Inicia el servidor y espera conexiones
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[INFO] Servidor iniciado en puerto: " + port);
            System.out.println("[INFO] Accede a http://localhost:" + port + "/index.html");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra el servidor
     */
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[INFO] Servidor detenido");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error al detener el servidor: " + e.getMessage());
        }
    }

    /**
     * Método principal
     */
    public static void main(String[] args) {
        int port = 8080; 
        WebServer server = new WebServer(port);
        server.start();
    }
}
