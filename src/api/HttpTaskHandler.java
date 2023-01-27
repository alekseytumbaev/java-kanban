package api;

import api.task_request_processors.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import constant.HttpCode;
import task_managers.Managers;
import task_managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static constant.HttpCode.BAD_REQUEST;
import static constant.HttpCode.OK;

public class HttpTaskHandler implements HttpHandler {

    private final AbstractTasksRequestProcessor taskRequestProcessor;
    private final AbstractTasksRequestProcessor subtaskRequestProcessor;
    private final AbstractTasksRequestProcessor epicRequestProcessor;
    private final AbstractTasksRequestProcessor historyRequestProcessor;
    private final AbstractTasksRequestProcessor defaultRequestProcessor;

    public HttpTaskHandler() {
        TaskManager taskManager = Managers.getDefault();
        taskRequestProcessor = new TaskRequestProcessor(taskManager);
        subtaskRequestProcessor = new SubtaskRequestProcessor(taskManager);
        epicRequestProcessor = new EpicRequestProcessor(taskManager);
        historyRequestProcessor = new HistoryRequestProcessor(taskManager);
        defaultRequestProcessor = new DefaultRequestProcessor(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String pathStr = requestURI.getPath();
        String[] path = pathStr.split("/");
        String query = requestURI.getQuery();
        String method = exchange.getRequestMethod();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        Response response = processRequest(method, path, query, body);

        HttpCode httpCode = response.getHttpCode();
        if (httpCode.equals(OK))
            exchange.getResponseHeaders().set("Content-type", "application/json");

        exchange.sendResponseHeaders(httpCode.code, 0);

        String responseBody = response.getBody();
        if (!responseBody.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }
        }

        exchange.close();
    }

    private Response processRequest(String method, String[] path, String query, String body) {
        if (path.length < 3)
            return defaultRequestProcessor.process(method, path, query, body);
        switch (path[2]) {
            case "task":
                return taskRequestProcessor.process(method, path, query, body);
            case "subtask":
                return subtaskRequestProcessor.process(method, path, query, body);
            case "epic":
                return epicRequestProcessor.process(method, path, query, body);
            case "history":
                return historyRequestProcessor.process(method, path, query, body);
            default:
                return new Response(BAD_REQUEST);
        }
    }
}