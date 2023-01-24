package task_managers.http_task_manager.kv;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient client;
    private final String API_TOKEN;
    private final String serverAddress;
    private final Gson gson;

    public KVTaskClient(String serverAddress) {
        gson = new Gson();
        client = HttpClient.newHttpClient();
        this.serverAddress = serverAddress;

        URI uri = URI.create(serverAddress + "/register");
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
        if (response.statusCode() == 200) {
            API_TOKEN = gson.fromJson(response.body(), String.class);
        } else {
            System.out.println("Ошибка при регистрации клиента, код " + response.statusCode());
            API_TOKEN = "";
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(serverAddress + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
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

        if (response.statusCode() != 200) {
            System.out.println("Ошибка при save(), код  " + response.statusCode());
        }
    }

    public String load(String key) {
        URI uri = URI.create(serverAddress + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
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

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.out.println("Ошибка при load(), код " + response.statusCode());
            return null;
        }
    }
}