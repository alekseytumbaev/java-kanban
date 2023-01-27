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

import static constant.Endpoint.*;
import static constant.HttpCode.*;
import static constant.HttpMethod.GET;
import static constant.HttpMethod.POST;
import static constant.Port.KV_SERVER;
import static java.nio.charset.StandardCharsets.UTF_8;


public class KVServer {
    private final StringBuilder logger;
    private final String ls = System.lineSeparator();
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        logger = new StringBuilder();
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", KV_SERVER.port), 0);
        server.createContext(REGISTER.url, this::register);
        server.createContext(SAVE.url, this::save);
        server.createContext(LOAD.url, this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try {
            logger.append(ls).append(LOAD.url).append(ls);
            if (!hasAuth(h)) {
                logger.append("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа").append(ls);
                h.sendResponseHeaders(UNAUTHORIZED.code, 0);
                return;
            }
            if (GET.toString().equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring((LOAD.url + "/").length());
                if (key.isEmpty()) {
                    logger.append("Key для получения пустой. key указывается в пути: ").
                            append(LOAD.url).append("/{key}").append(ls);
                    h.sendResponseHeaders(BAD_REQUEST.code, 0);
                    return;
                }
                if (!data.containsKey(key)) {
                    logger.append("Указанного ключа не существует. Ключ: ").append(key).append(ls);
                    h.sendResponseHeaders(NOT_FOUND.code, 0);
                    return;
                }
                String value = data.get(key);
                logger.append("Значение для ключа получено. Ключ: ").append(key).append(ls);
                sendText(h, value);
            } else {
                logger.append(LOAD.url).append("ждёт GET-запрос, а получил: ").append(h.getRequestMethod()).append(ls);
                h.sendResponseHeaders(METHOD_NOT_ALLOWED.code, 0);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            logger.append(ls).append(SAVE.url).append(ls);
            if (!hasAuth(h)) {
                logger.append("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа").append(ls);
                h.sendResponseHeaders(UNAUTHORIZED.code, 0);
                return;
            }
            if (POST.toString().equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring((SAVE.url + "/").length());
                if (key.isEmpty()) {
                    logger.append("Key для сохранения пустой. key указывается в пути: ").
                            append(SAVE.url).append("/{key}").append(ls);
                    h.sendResponseHeaders(BAD_REQUEST.code, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    logger.append("Value для сохранения пустой. value указывается в теле запроса").append(ls);
                    h.sendResponseHeaders(BAD_REQUEST.code, 0);
                    return;
                }
                data.put(key, value);
                logger.append("Значение для ключа ").append(key).append(" успешно обновлено!").append(ls);
                h.sendResponseHeaders(OK.code, 0);
            } else {
                logger.append(SAVE.url).append(" ждёт POST-запрос, а получил: ").append(h.getRequestMethod()).append(ls);
                h.sendResponseHeaders(METHOD_NOT_ALLOWED.code, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            logger.append(ls).append(REGISTER.url).append(ls);
            if (GET.toString().equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                logger.append(REGISTER.url).append("ждёт GET-запрос, а получил ").append(h.getRequestMethod()).append(ls);
                h.sendResponseHeaders(METHOD_NOT_ALLOWED.code, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        logger.append("Запускаем сервер на порту ").append(KV_SERVER.port).append(ls);
        logger.append("Открой в браузере http://localhost:").append(KV_SERVER.port).append("/").append(ls);
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
        h.sendResponseHeaders(OK.code, resp.length);
        h.getResponseBody().write(resp);
    }
}