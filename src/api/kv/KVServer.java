package api.kv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


public class KVServer {
    private final StringBuilder logger;
    private final String ls = System.lineSeparator();
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        logger = new StringBuilder();
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try {
            logger.append("\n/load").append(ls);
            if (!hasAuth(h)) {
                logger.append("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа").append(ls);
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    logger.append("Key для получения пустой. key указывается в пути: /load/{key}").append(ls);
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if (!data.containsKey(key)) {
                    logger.append("Указанного ключа не существует. Ключ: ").append(key).append(ls);
                    h.sendResponseHeaders(404, 0);
                    return;
                }
                String value = data.get(key);
                logger.append("Значение для ключа получено. Ключ: ").append(key).append(ls);
                sendText(h, value);
            } else {
                logger.append("/load ждёт GET-запрос, а получил: ").append(h.getRequestMethod()).append(ls);
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            logger.append("\n/save").append(ls);
            if (!hasAuth(h)) {
                logger.append("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа").append(ls);
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    logger.append("Key для сохранения пустой. key указывается в пути: /save/{key}").append(ls);
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    logger.append("Value для сохранения пустой. value указывается в теле запроса").append(ls);
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                logger.append("Значение для ключа ").append(key).append(" успешно обновлено!").append(ls);
                h.sendResponseHeaders(200, 0);
            } else {
                logger.append("/save ждёт POST-запрос, а получил: ").append(h.getRequestMethod()).append(ls);
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            logger.append("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                logger.append("/register ждёт GET-запрос, а получил ").append(h.getRequestMethod()).append(ls);
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        logger.append("Запускаем сервер на порту " + PORT).append(ls);
        logger.append("Открой в браузере http://localhost:").append(PORT).append("/").append(ls);
        logger.append("API_TOKEN: ").append(apiToken).append(ls);
        server.start();
    }

    public void stop() {
        server.stop(0);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("kvServerLogs.txt"))) {
            bw.write(logger.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить лог kv сервера");
        }
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}