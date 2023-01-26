package task_managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    void beforeEach() {
        taskManager = new FileBackedTaskManager();
    }

    @Test
    void testSavingAndLoadingEmptyEpic() {
        Epic epic = new Epic("эпик", "описание эпика");
        taskManager.addEpic(epic);

        TaskManager fbm = Managers.loadFromFile(new File("tasks.csv"));
        assertEquals(
                taskManager.getEpicById(epic.getId()),
                fbm.getEpicById(epic.getId()),
                "Эпики менеджеров не запускают");
    }

    @Test
    void testSavingAndLoadingEmptyTasksAndHistory() {
        Epic epic = new Epic("эпик", "описание эпика");
        taskManager.addEpic(epic);
        taskManager.deleteEpicById(epic.getId());

        TaskManager fbm = Managers.loadFromFile(new File("tasks.csv"));

        assertEquals(taskManager.getAllTasks().size(), 0, "Список задач не пустой");
        assertEquals(taskManager.getHistory().size(), 0, "История не пустая");
        assertEquals(
                taskManager.getAllTasks(),
                fbm.getAllTasks(),
                "Не совпадают списки задач");
        assertEquals(
                taskManager.getHistory(),
                fbm.getHistory(),
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

        FileBackedTaskManager fbm = Managers.loadFromFile(new File("tasks.csv"));

        assertEquals(taskManager.getCurrentId(), fbm.getCurrentId(), "Не совпадают id");
        assertEquals(
                taskManager.getAllEpics(),
                fbm.getAllEpics(),
                "Не совпадают эпики");
        assertEquals(
                taskManager.getHistory(),
                fbm.getHistory(),
                "Не совпадают истории");
        assertEquals(
                taskManager.getPrioritizedTasks(),
                fbm.getPrioritizedTasks(),
                "Списки приоритетов не совпадают");
    }
}