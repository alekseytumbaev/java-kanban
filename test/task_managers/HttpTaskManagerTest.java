package task_managers;

import api.kv.KVServer;
import api.kv.KVTaskClient;
import constant.Endpoint;
import constant.Port;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest {

    private static final String URL = Endpoint.HOST.url + Port.KV_SERVER.port;
    private KVServer kvServer;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(new KVTaskClient(URL), "key");
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void testSavingAndLoadingEmptyEpic() {
        Epic epic = new Epic("эпик", "описание эпика");
        taskManager.addEpic(epic);

        TaskManager httpm = Managers.loadFromKVServer(URL, "key");
        assertEquals(
                taskManager.getEpicById(epic.getId()),
                httpm.getEpicById(epic.getId()),
                "Эпики менеджеров не запускают");
    }

    @Test
    void testSavingAndLoadingEmptyTasksAndHistory() {
        Epic epic = new Epic("эпик", "описание эпика");
        taskManager.addEpic(epic);
        taskManager.deleteEpicById(epic.getId());

        TaskManager httpm = Managers.loadFromKVServer(URL, "key");

        assertEquals(taskManager.getAllTasks().size(), 0, "Список задач не пустой");
        assertEquals(taskManager.getHistory().size(), 0, "История не пустая");
        assertEquals(
                taskManager.getAllTasks(),
                httpm.getAllTasks(),
                "Не совпадают списки задач");
        assertEquals(
                taskManager.getHistory(),
                httpm.getHistory(),
                "Не совпадают истории");
    }

    @Test
    void testSavingAndLoading() {
        Subtask sub1 = new Subtask("подзача 1", "описание подзадачи 1", now, 1);
        Subtask sub2 = new Subtask("подзача 2", "описание подзадачи 2", now.plusSeconds(60), 1);
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Epic epic = new Epic("эпик", "описание эпика");
        epic.addSubtaskId(sub1.getId());
        epic.addSubtaskId(sub2.getId());
        taskManager.addEpic(epic);

        Task task = new Task("задача", "описание", now.plusSeconds(120), 1);
        taskManager.addTask(task);

        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(sub1.getId());
        taskManager.getTaskById(task.getId());

        TaskManager httpm = Managers.loadFromKVServer(URL, "key");

        assertEquals(taskManager.getCurrentId(), httpm.getCurrentId(), "Не совпадают id");
        assertEquals(
                taskManager.getAllEpics(),
                httpm.getAllEpics(),
                "Не совпадают эпики");
        assertEquals(
                taskManager.getHistory(),
                httpm.getHistory(),
                "Не совпадают истории");
        assertEquals(
                taskManager.getPrioritizedTasks(),
                httpm.getPrioritizedTasks(),
                "Списки приоритетов не совпадают");
    }

}