package api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private HttpServer server;

    public HttpTaskServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать сервер: " + e.getMessage());
        }
        server.createContext("/tasks", new HttpTaskHandler());
        server.start();
    }
}