package api.kv;

import com.google.gson.Gson;
import exception.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static constant.Endpoint.*;
import static constant.HttpCode.OK;

public class KVTaskClient {

    private final StringBuilder logger;
    private final String ls = System.lineSeparator();
    private final HttpClient client;
    private final String API_TOKEN;
    private final String serverAddress;
    private final Gson gson;

    public KVTaskClient(String serverAddress) {
        logger = new StringBuilder();
        gson = new Gson();
        client = HttpClient.newHttpClient();
        this.serverAddress = serverAddress;

        URI uri = URI.create(serverAddress + REGISTER.url);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        final HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == OK.code) {
            API_TOKEN = gson.fromJson(response.body(), String.class);
        } else {
            logger.append("Ошибка при регистрации клиента, код ").append(response.statusCode()).append(ls);
            API_TOKEN = "";
            writeLogs();
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(serverAddress + SAVE.url + "/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();

        final HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() != OK.code) {
            logger.append("Ошибка при save(), код  ").append(response.statusCode()).append(ls);
            writeLogs();
            
        }
    }

    public String load(String key) {
        URI uri = URI.create(serverAddress + LOAD.url + "/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        final HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == OK.code) {
            return response.body();
        } else {
            logger.append("Ошибка при load(), код ").append(response.statusCode()).append(ls);
            writeLogs();
            return null;
        }
        
    }
    
    private void writeLogs() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("kvClientLogs.txt"))) {
            bw.write(logger.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить лог kv сервера");
        }
    }
}