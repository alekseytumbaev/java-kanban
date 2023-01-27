package api;

import api.kv.KVServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static constant.Endpoint.*;
import static constant.HttpCode.*;
import static constant.Port.TASK_SERVER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {

    static final String URL = HOST.url + TASK_SERVER.port + MAIN.url;
    static final String subURL = URL + SUBTASK.url;
    static final String taskURL = URL + TASK.url;
    static final String epicURL = URL + EPIC.url;

    static HttpClient client;
    static Gson gson;
    static Instant now;
    KVServer kvServer;
    HttpTaskServer taskServer;

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
        now = Instant.now();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void taskEndpointsTest() {
        Task task1 = new Task("task1", "desc", now, 1);
        String taskStr1 = gson.toJson(task1);

        //POST
        HttpResponse<String> postResp = sendPost(taskStr1, taskURL);
        assertEquals(OK.code, postResp.statusCode(), "Неверный код ответа при POST");
        Task addedTask = gson.fromJson(postResp.body(), Task.class);
        task1.setId(addedTask.getId());
        assertEquals(task1, addedTask, "Задачи при POST-запросе не совпадают");

        //GET by id
        HttpResponse<String> getByIdResp = sendGet(taskURL + "?id=" + task1.getId());
        assertEquals(OK.code, getByIdResp.statusCode(), "Неверный код ответа при GET by id");
        assertEquals(task1, gson.fromJson(getByIdResp.body(), Task.class), "Задачи при GET-запросе по id не совпадают");

        //GET
        HttpResponse<String> getResp = sendGet(taskURL);
        assertEquals(OK.code, getResp.statusCode(), "Неверный код ответа при GET all");
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        assertEquals(List.of(task1), gson.fromJson(getResp.body(), type), "Задачи при GET-запросе не совпадают");

        //DELETE by id
        HttpResponse<String> deleteByIdResp = sendDelete(taskURL + "?id=" + task1.getId());
        assertEquals(NO_CONTENT.code, deleteByIdResp.statusCode(), "Неверный код ответа при DELETE by id");
        HttpResponse<String> getRespAfterDel = sendGet(taskURL + "?id=" + task1.getId());
        assertEquals(NOT_FOUND.code, getRespAfterDel.statusCode(),
                "Задача при DELETE-запросе по id не удаляется");

        //DELETE
        String taskStr2 = gson.toJson(new Task("task2", "desc", now, 2));
        sendPost(taskStr2, taskURL);
        HttpResponse<String> deleteResp = sendDelete(taskURL);
        assertEquals(NO_CONTENT.code, deleteResp.statusCode(), "Неверный код ответа при DELETE");
        HttpResponse<String> getRespAfterDelAll = sendGet(taskURL);
        assertEquals(new ArrayList<Task>(), gson.fromJson(getRespAfterDelAll.body(), type),
                "Не все задачи удалены");
    }

    @Test
    void subtaskEndpointsTest() {
        Epic epic = new Epic("epic", "desc");
        Subtask sub1 = new Subtask("sub1", "desc", now, 1);
        String subStr1 = gson.toJson(sub1);

        //POST
        //Для корректного добавления подзадачи необходимо:
        //1) Добавить подзадачу в менеджер, чтобы он назначил ей id
        HttpResponse<String> postResp = sendPost(subStr1, subURL);
        assertEquals(OK.code, postResp.statusCode(), "Неверный код ответа при добавлении подзадачи POST-запросом");

        //2) Добавить id подзадачи в эпик
        Subtask tmpSub = gson.fromJson(postResp.body(), Subtask.class);
        epic.addSubtaskId(tmpSub.getId());

        //3) Добавить эпик
        HttpResponse<String> postAddEpicResp = sendPost(gson.toJson(epic), epicURL);
        assertEquals(OK.code, postAddEpicResp.statusCode(),
                "Неверный код ответа при добавлении эпика с подзадачей POST-запросом");

        //GET by id
        HttpResponse<String> getByIdResp = sendGet(subURL + "?id=" + tmpSub.getId());
        assertEquals(OK.code, getByIdResp.statusCode(), "Неверный код ответа при GET by id");
        Subtask addedSub = gson.fromJson(getByIdResp.body(), Subtask.class);
        tmpSub.setEpicId(addedSub.getEpicId());
        assertEquals(tmpSub, addedSub, "Подзадачи при GET-запросе не совпадают");

        //GET by epicId
        HttpResponse<String> getByEpicIdResp = sendGet(subURL + EPIC.url.substring(1) + "?id=" + addedSub.getEpicId());
        assertEquals(OK.code, getByEpicIdResp.statusCode(), "Неверный код ответа при GET by epicId");
        Type type = new TypeToken<ArrayList<Subtask>>() {}.getType();
        assertEquals(List.of(addedSub), gson.fromJson(getByEpicIdResp.body(), type),
                "Подзадачи при GET by epicId не совпадают");

        //GET
        HttpResponse<String> getResp = sendGet(subURL);
        assertEquals(OK.code, getResp.statusCode(), "Неверный код ответа при GET all");
        assertEquals(List.of(addedSub), gson.fromJson(getResp.body(), type), "Подзадачи при GET all не совпадают");

        //DELETE by id
        HttpResponse<String> deleteByIdResp = sendDelete(subURL + "?id=" + addedSub.getId());
        assertEquals(NO_CONTENT.code, deleteByIdResp.statusCode(), "Неверный код ответа при DELETE by id");
        HttpResponse<String> getRespAfterDel = sendGet(subURL + "?id=" + addedSub.getId());
        assertEquals(NOT_FOUND.code, getRespAfterDel.statusCode(),
                "Подзадача при DELETE-запросе по id не удаляется");

        //DELETE
        String subStr2 = gson.toJson(new Subtask("sub", "desc", now, 2));
        sendPost(subStr2, subURL);
        HttpResponse<String> deleteResp = sendDelete(subURL);
        assertEquals(NO_CONTENT.code, deleteResp.statusCode(), "Неверный код ответа при DELETE");
        HttpResponse<String> getRespAfterDelAll = sendGet(subURL);
        assertEquals(new ArrayList<Subtask>(), gson.fromJson(getRespAfterDelAll.body(), type),
                "Не все подзадачи удалены");
    }

    @Test
    void epicEndpointsTest() {
        Epic epic1 = new Epic("epic1", "desc");
        Subtask sub1 = new Subtask("sub1", "desc", now, 1);
        String subStr1 = gson.toJson(sub1);

        HttpResponse<String> postSub1Resp = sendPost(subStr1, subURL);
        Subtask addedSub1 = gson.fromJson(postSub1Resp.body(), Subtask.class);
        epic1.addSubtaskId(addedSub1.getId());

        //POST
        HttpResponse<String> postEpic1Resp = sendPost(gson.toJson(epic1), epicURL);
        assertEquals(OK.code, postEpic1Resp.statusCode(),
                "Неверный код ответа при добавлении перового эпика с подзадачей POST-запросом");
        Epic addedEpic1 = gson.fromJson(postEpic1Resp.body(), Epic.class);
        addedSub1.setEpicId(addedEpic1.getId());
        epic1.setId(addedEpic1.getId());
        assertEquals(epic1, addedEpic1, "Эпики при POST-запросе не совпадают");

        //GET by id
        HttpResponse<String> getByIdResp = sendGet(epicURL + "?id=" + addedEpic1.getId());
        assertEquals(OK.code, postEpic1Resp.statusCode(),
                "Неверный код ответа при получении эпика с подзадачей GET-запросом по id");
        assertEquals(addedEpic1, gson.fromJson(getByIdResp.body(), Epic.class),
                "Добавленный и полученный эпик не совпадают");

        //GET
        HttpResponse<String> getResp = sendGet(epicURL);
        assertEquals(OK.code, getResp.statusCode(), "Неверный код ответа при GET all");
        Type type = new TypeToken<ArrayList<Epic>>() {}.getType();
        assertEquals(List.of(addedEpic1), gson.fromJson(getResp.body(), type), "Эпики при GET all не совпадают");

        //DELETE by id
        HttpResponse<String> deleteByIdResp = sendDelete(epicURL + "?id=" + epic1.getId());
        assertEquals(NO_CONTENT.code, deleteByIdResp.statusCode(), "Неверный код ответа при DELETE by id");
        HttpResponse<String> getRespAfterDel = sendGet(epicURL + "?id=" + epic1.getId());
        assertEquals(NOT_FOUND.code, getRespAfterDel.statusCode(),
                "Эпик при DELETE-запросе по id не удаляется");

        HttpResponse<String> getSubRespAfterDel = sendGet(subURL + "?id=" + addedSub1.getId());
        assertEquals(NOT_FOUND.code, getSubRespAfterDel.statusCode(),
                "Подзадача эпика при DELETE-запросе по id не удаляется");

        //DELETE
        Epic epic2 = new Epic("epic1", "desc");
        Subtask sub2 = new Subtask("sub1", "desc", now, 1);
        String subStr2 = gson.toJson(sub2);

        HttpResponse<String> postSub2Resp = sendPost(subStr2, subURL);
        Subtask addedSub2 = gson.fromJson(postSub2Resp.body(), Subtask.class);
        epic2.addSubtaskId(addedSub2.getId());

        HttpResponse<String> postEpic2Resp = sendPost(gson.toJson(epic2), epicURL);
        Epic addedEpic2 = gson.fromJson(postEpic2Resp.body(), Epic.class);
        addedSub2.setEpicId(addedEpic2.getId());

        HttpResponse<String> deleteEpicResp = sendDelete(epicURL);
        assertEquals(NO_CONTENT.code, deleteEpicResp.statusCode(), "Неверный статус при удалении эпиков");
        HttpResponse<String> getEpicsAfterDel = sendGet(epicURL);
        assertEquals(new ArrayList<Epic>(), gson.fromJson(getEpicsAfterDel.body(), type),
                "Не все эпики удалены");
        HttpResponse<String> getSubsAfterDel = sendGet(subURL);
        assertEquals(new ArrayList<Subtask>(), gson.fromJson(getSubsAfterDel.body(), type),
                "Не все подзадачи удалены");
    }

    @Test
    void historyAndPrioritizedEndpointsTest() {
        Task task1 = new Task("task1", "desc", now, 1);
        String taskStr1 = gson.toJson(task1);
        Task task2 = new Task("task2", "desc", now.plusSeconds(60), 2);
        String taskStr2 = gson.toJson(task2);

        HttpResponse<String> postResp1 = sendPost(taskStr1, taskURL);
        Task tmp1 = gson.fromJson(postResp1.body(), Task.class);
        HttpResponse<String> getResp1 = sendGet(taskURL + "?id=" + tmp1.getId());
        Task added1 = gson.fromJson(getResp1.body(), Task.class);

        HttpResponse<String> postResp2 = sendPost(taskStr2, taskURL);
        Task tmp2 = gson.fromJson(postResp2.body(), Task.class);
        HttpResponse<String> getResp2 = sendGet(taskURL + "?id=" + tmp2.getId());
        Task added2 = gson.fromJson(getResp2.body(), Task.class);

        //History
        HttpResponse<String> historyResp = sendGet(URL + "history");
        assertEquals(OK.code, historyResp.statusCode(), "Неверный код при получении истории");
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> history = gson.fromJson(historyResp.body(), type);
        assertEquals(List.of(added1, added2), history);

        //Prioritized
        HttpResponse<String> getPriorResp = sendGet(URL);
        assertEquals(OK.code, getPriorResp.statusCode(), "Неверный код при получении приоритетных задач");
        List<Task> prioritized = gson.fromJson(getPriorResp.body(), type);
        assertEquals(List.of(added1, added2), prioritized);
    }


    private HttpResponse<String> sendPost(String bodyStr, String urlStr) {
        URI url = URI.create(urlStr);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(bodyStr);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> sendGet(String urlStr) {
        URI url = URI.create(urlStr);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> sendDelete(String urlStr) {
        URI url = URI.create(urlStr);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}