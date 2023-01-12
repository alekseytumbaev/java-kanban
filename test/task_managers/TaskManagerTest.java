package task_managers;

import constant.TaskStatus;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest {

    TaskManager taskManager;

    //********************Tasks*****************************************************************************************
    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test updateTask", "Test updateTask description");
        taskManager.addTask(task);

        Task updatedTask = new Task(
                task.getId(),
                "Test updated task",
                "Test updated task description",
                TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Task retrievedUpdatedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedUpdatedTask, "Задача не найдена");
        assertEquals(updatedTask, retrievedUpdatedTask, "Задачи не совпадают");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(updatedTask, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void deleteTask() {
        Task task1 = new Task("Test delete task", "Test delete task description");
        taskManager.addTask(task1);

        taskManager.deleteTaskById(task1.getId());
        assertNull(taskManager.getTaskById(task1.getId()), "Задача не удаляется");

        Task task2 = new Task("Test delete task", "Test delete task description");
        Task task3 = new Task("Test delete task", "Test delete task description");
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.deleteAllTasks();
        assertNull(taskManager.getTaskById(task2.getId()), "Не все задачи удалились");
        assertNull(taskManager.getTaskById(task3.getId()), "Не все задачи удалились");
    }
    //******************************************************************************************************************

    //********************Subtasks**************************************************************************************
    //Для корректного добавления эпика сначала необходимо добавить подзадачи в manager, потом их id в сам эпик

    @Test
    void addSubtask() {
        Subtask subtask = new Subtask("Test subtask", "Test subtask description");
        taskManager.addSubtask(subtask);
        Epic epic = new Epic("Test epic for subtask", "Test epic for subtask description");
        epic.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic);

        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(savedSubtask, subtask, "Подзадачи не совпадают");
        assertEquals(savedSubtask.getEpicId(), epic.getId(), "Неверное id эпика");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество подзадачи");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void updateSubtask() {
        Subtask subtask = new Subtask("Test subtask", "Test subtask description");
        taskManager.addSubtask(subtask);
        Epic epic = new Epic("Test epic for subtask", "Test epic for subtask description");
        epic.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic);

        Subtask updatedSubtask = new Subtask(
                subtask.getId(),
                "Test updated subtask",
                "Test updated subtask description",
                TaskStatus.IN_PROGRESS,
                subtask.getEpicId());
        taskManager.updateTask(updatedSubtask);

        Subtask retrievedUpdatedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedUpdatedSubtask, "Подзадача не найдена");
        assertEquals(subtask, retrievedUpdatedSubtask, "Подзадачи не совпадают");
        assertEquals(retrievedUpdatedSubtask.getEpicId(), epic.getId(), "Неверное id эпика");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество подзадачи");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void deleteSubtask() {
        Subtask subtask1 = new Subtask("Test subtask1", "Test subtask1 description");
        taskManager.addSubtask(subtask1);
        Epic epic = new Epic("Test epic for subtask1", "Test epic for subtask1 description");
        epic.addSubtaskId(subtask1.getId());
        taskManager.addEpic(epic);

        taskManager.deleteSubtaskById(subtask1.getId());
        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача не удаляется");

        Subtask subtask2 = new Subtask("Test subtask2", "Test subtask2 description");
        Subtask subtask3 = new Subtask("Test subtask3", "Test subtask3 description");
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        epic.addSubtaskId(subtask2.getId());
        epic.addSubtaskId(subtask3.getId());
        taskManager.addEpic(epic);


        taskManager.deleteAllSubtasks();
        assertNull(taskManager.getSubtaskById(subtask2.getId()), "Не все задачи удалились");
        assertNull(taskManager.getSubtaskById(subtask3.getId()), "Не все задачи удалились");
    }

    //***********************Epics**************************************************************************************
    //Для корректного добавления эпика сначала необходимо добавить подзадачи в manager, потом их id в сам эпик
    @Test
    void addEpic() {
        Subtask subtask = new Subtask("Test subtask for epic", "Test subtask for epic description");
        taskManager.addSubtask(subtask);
        Epic epic = new Epic("Test epic", "Test epic");
        epic.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW, "Неверный статус эпика");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void updateEpic() {
        Subtask subtask = new Subtask("Test subtask for epic", "Test subtask for epic description");
        taskManager.addSubtask(subtask);
        Epic epic = new Epic("Test epic", "Test epic");
        epic.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(),
                "Test updated task",
                "Test updated task description",
                TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(updatedEpic);

        Task retrievedUpdatedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedUpdatedEpic, "Эпик не найден");
        assertEquals(retrievedUpdatedEpic, updatedEpic, "Задачи не совпадают");

        List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(retrievedUpdatedEpic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void deleteEpic() {
        Subtask subtask = new Subtask("Test subtask for epic", "Test subtask for epic description");
        taskManager.addSubtask(subtask);
        Epic epic = new Epic("Test epic", "Test epic");
        epic.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic);

        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не удаляется");
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача эпика не удаляется");

        Subtask subtask1 = new Subtask("Test subtask1 for epic1", "Test subtask1 for epic1 description");
        taskManager.addSubtask(subtask);
        Epic epic1 = new Epic("Test epic1", "Test epic1");
        epic1.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic1);

        Subtask subtask2 = new Subtask("Test subtask2 for epic2", "Test subtask2 for epic2 description");
        taskManager.addSubtask(subtask);
        Epic epic2 = new Epic("Test epic2", "Test epic2");
        epic2.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic2);

        taskManager.deleteAllEpics();
        assertNull(taskManager.getEpicById(epic1.getId()), "Не все эпики удалились");
        assertNull(taskManager.getEpicById(epic2.getId()), "Не все эпики удалились");
        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Не все подзадачи удалились");
        assertNull(taskManager.getSubtaskById(subtask2.getId()), "Не все подзадачи удалились");
    }

    @Test
    void getEpicSubtasks() {
        Subtask subtask = new Subtask("Test subtask for epic", "Test subtask for epic description");
        taskManager.addSubtask(subtask);
        Epic epic = new Epic("Test epic", "Test epic");
        epic.addSubtaskId(subtask.getId());
        taskManager.addEpic(epic);

        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertNotNull(subtasks, "Не возвращается список подзадач");
        assertEquals(1, subtasks.size(), "Неверный размер списка подзадач");
        assertEquals(subtask, subtasks.get(0), "Неверная подзадача в списке");
    }

    @Test
    void epicWithEmptySubtasks() {
        Epic epic = new Epic("Test epic", "Test epic");
        taskManager.addEpic(epic);
        assertEquals(epic.getStatus(), TaskStatus.NEW);
    }

    @Test
    void epicWithDifferentStatusSubtasks() {
        //все со статусом NEW
        Subtask subtask1 = new Subtask("Test subtask1 for epic", "Test subtask1 for epic description");
        Subtask subtask2 = new Subtask("Test subtask2 for epic", "Test subtask2 for epic description");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic epic = new Epic("Test epic", "Test epic");
        epic.addSubtaskId(subtask1.getId());
        epic.addSubtaskId(subtask2.getId());
        taskManager.addEpic(epic);

        assertEquals(epic.getStatus(), TaskStatus.NEW);

        //DONE и NEW
        Subtask doneSubtask1 = new Subtask(
                subtask1.getId(),
                "updated subtask",
                "updated subtask description",
                TaskStatus.DONE,
                subtask1.getEpicId());
        taskManager.updateSubtask(doneSubtask1);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);


        //все DONE
        Subtask doneSubtask2 = new Subtask(
                subtask2.getId(),
                "updated subtask",
                "updated subtask description",
                TaskStatus.DONE,
                subtask1.getEpicId());
        taskManager.updateSubtask(doneSubtask2);

        assertEquals(epic.getStatus(), TaskStatus.DONE);


        //все IN_PROGRESS
        Subtask inProgressSubtask1 = new Subtask(
                subtask1.getId(),
                "updated subtask",
                "updated subtask description",
                TaskStatus.IN_PROGRESS,
                subtask1.getEpicId());
        taskManager.updateSubtask(inProgressSubtask1);


        Subtask inProgressSubtask2 = new Subtask(
                subtask2.getId(),
                "updated subtask",
                "updated subtask description",
                TaskStatus.IN_PROGRESS,
                subtask2.getEpicId());
        taskManager.updateSubtask(inProgressSubtask2);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
    }
    //******************************************************************************************************************
}