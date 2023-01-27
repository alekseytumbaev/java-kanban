package api;

import com.sun.net.httpserver.HttpServer;
import constant.Endpoint;
import constant.Port;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {

    private HttpServer server;

    public HttpTaskServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(Port.TASK_SERVER.port), 0);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать сервер: " + e.getMessage());
        }
        server.createContext(Endpoint.MAIN.url, new HttpTaskHandler());
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}