import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("1 обычная задача", "1 обычное описание", TaskStatus.NEW);
        Task task2 = new Task("2 обычная задача", "2 обычное описание", TaskStatus.NEW);

        manager.createTask(task1);
        System.out.println("Создана задача: " + manager.getTaskById(task1.getId()));
        manager.createTask(task2);
        System.out.println("Создана задача: " + manager.getTaskById(task2.getId()));

        Task updatedTask = new Task("1 обновленная обычная задача",
                "1 обновлено обычное описание",task1.getStatus());
        updatedTask.setId(task1.getId());
        manager.updateTask(updatedTask);
        System.out.println("\nОбновлена задача: " + manager.getTaskById(task1.getId()));

        System.out.println("\nВсе обычные задачи");
        System.out.println(manager.getAllTasks());

        manager.deleteTaskById(task1.getId());
        System.out.println("\nЗадача 1 удалена. Все задачи:");
        System.out.println(manager.getAllTasks());

        manager.deleteAllTasks();
        System.out.println("\nВсе задачи удалены.Все задачи:");
        System.out.println(manager.getAllTasks());
    }
}
