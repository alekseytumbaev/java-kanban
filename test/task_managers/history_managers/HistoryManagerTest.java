package task_managers.history_managers;

import constant.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addOneTask() {
        Task task = new Task(0,"название", "описание", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История = null");
        assertEquals(1, history.size(), "Неверный размер истории");
    }

    @Test
    void addDuplicatedTasks() {
        Task task = new Task(0,"название", "описание", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История = null");
        assertEquals(1, history.size(), "Неверный размер истории");
    }

    @Test
    void getEmptyHistory() {
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История = null");
        assertEquals(0, history.size(), "Неверный размер истории");
    }

    @Test
    void removeFromEnd() {
        Task task = new Task(0,"название", "описание", TaskStatus.NEW);
        Task task1 = new Task(1,"название1", "описание", TaskStatus.NEW);
        Task task2 = new Task(2,"название2", "описание", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task.getId());

        assertEquals(List.of(task2,task1),historyManager.getHistory(), "Не сработало удаление с конца");
    }

    @Test
    void removeFromMiddle() {
        Task task = new Task(0,"название", "описание", TaskStatus.NEW);
        Task task1 = new Task(1,"название1", "описание", TaskStatus.NEW);
        Task task2 = new Task(2,"название2", "описание", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        assertEquals(List.of(task2,task),historyManager.getHistory(), "Не сработало удаление из середины");
    }

    @Test
    void removeFromBeginning() {
        Task task = new Task(0,"название", "описание", TaskStatus.NEW);
        Task task1 = new Task(1,"название1", "описание", TaskStatus.NEW);
        Task task2 = new Task(2,"название2", "описание", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        assertEquals(List.of(task1,task),historyManager.getHistory(), "Не сработало удаление с начала");
    }
}