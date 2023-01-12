package task_managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}