package task_managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

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
        Subtask fsub1 = new Subtask("подзача 1", "описание подзадачи 1");
        Subtask fsub2 = new Subtask("подзача 2", "описание подзадачи 2");
        taskManager.addSubtask(fsub1);
        taskManager.addSubtask(fsub2);

        Epic fepic = new Epic("эпик", "описание эпика");
        fepic.addSubtaskId(fsub1.getId());
        fepic.addSubtaskId(fsub2.getId());
        taskManager.addEpic(fepic);

        FileBackedTaskManager fbm = Managers.loadFromFile(new File("tasks.csv"));

        assertEquals(
                taskManager.getAllEpics(),
                fbm.getAllEpics(),
                "Не совпадают эпики");
        assertEquals(
                taskManager.getHistory(),
                fbm.getHistory(),
                "Не совпадают истории");
    }
}