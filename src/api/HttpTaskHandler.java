package api;

import api.processor.Response;
import api.processor.TaskRequestProcessor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_managers.Managers;
import task_managers.TaskManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HttpTaskHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final TaskRequestProcessor taskRequestProcessor;

    public HttpTaskHandler() {
        taskManager = Managers.loadFromFileOrGetNew(new File("tasks.csv"));
        taskRequestProcessor = new TaskRequestProcessor(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String pathStr = requestURI.getPath();
        String[] path = pathStr.split("/");
        String query = requestURI.getQuery();
        String method = exchange.getRequestMethod();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        Response response;
        switch (path[2]) {
            case "tasks":
                response = taskRequestProcessor.process(method, query, body);
                break;
            default:
                response = new Response(400);
                break;
        }

        int httpCode = response.getHttpCode();
        if (httpCode == 200)
            exchange.getResponseHeaders().set("Content-type", "application/json");

        exchange.sendResponseHeaders(httpCode,0);

        String responseBody = response.getBody();
        if (!responseBody.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }

        exchange.close();
    }
}