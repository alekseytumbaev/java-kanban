import task_managers.FileBackedTasksManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import constant.TaskStatus;
import task_managers.Managers;
import task_managers.TaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        /*
        TaskManager manager = Managers.getDefault();

        System.out.println("****************************Проверка обычных задач***************************************");
        Task task1 = new Task("1 обычная задача", "1 обычное описание");
        Task task2 = new Task("2 обычная задача", "2 обычное описание");

        manager.addTask(task1);
        System.out.println("Создана задача: " + manager.getTaskById(task1.getId()));
        manager.addTask(task2);
        System.out.println("Создана задача: " + manager.getTaskById(task2.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        Task updatedTask = new Task("1 обновленная обычная задача",
                "1 обновлено обычное описание");
        updatedTask.setStatus(task1.getStatus());
        updatedTask.setId(task1.getId());
        manager.updateTask(updatedTask);
        System.out.println("\nОбновлена задача: " + manager.getTaskById(task1.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        System.out.println("\nВсе обычные задачи");
        System.out.println(manager.getAllTasks());

        manager.deleteTaskById(task1.getId());
        System.out.println("\nЗадача 1 удалена. Все задачи:");
        System.out.println(manager.getAllTasks());
        System.out.println("История:");
        System.out.println(manager.getHistory());

        manager.deleteAllTasks();
        System.out.println("\nВсе задачи удалены.Все задачи:");
        System.out.println(manager.getAllTasks());
        System.out.println("История:");
        System.out.println(manager.getHistory());


        System.out.println("\n\n****************************Проверка эпиков**********************************************");
        //Для корректоного добавлеиня эпика сначала необходимо добавить подзадачи в manager, потом их id в сам эпик

        Subtask sub11 = new Subtask("1ая подзадача 1го эпика", "описание 11");
        Subtask sub21 = new Subtask("2ая подзадача 1го эпика", "описание 21");
        manager.addSubtask(sub11);
        manager.addSubtask(sub21);
        Epic epic1 = new Epic("1ый эпик", "описание 1го эпика");
        epic1.addSubtaskId(sub11.getId());
        epic1.addSubtaskId(sub21.getId());
        manager.addEpic(epic1);
        System.out.println("1ый эпик:");
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println("Подзадачи 1го эпика:");
        System.out.println(manager.getEpicSubtasks(epic1.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        Subtask sub2 = new Subtask("подзадача 2го эпика", "описание 2");
        manager.addSubtask(sub2);
        Epic epic2 = new Epic("2ой эпик", "описание 2го эпика");
        epic2.addSubtaskId(sub2.getId());
        manager.addEpic(epic2);
        System.out.println("2ой эпик:");
        System.out.println(manager.getEpicById(epic2.getId()));
        System.out.println("Подзадачи 2го эпика:");
        System.out.println(manager.getEpicSubtasks(epic2.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        Subtask updatedSub11 = new Subtask("обновленная подзадача 1 эпика", "описание");
        updatedSub11.setStatus(TaskStatus.IN_PROGRESS);
        updatedSub11.setId(sub11.getId());
        manager.updateSubtask(updatedSub11);
        System.out.println("\nИзменили статус подзадачи первого эпика на IN_PROGRESS");
        System.out.println("1ый эпик:");
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println("Подзадачи 1го эпика:");
        System.out.println(manager.getEpicSubtasks(epic1.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        Subtask updatedSub2 = new Subtask("обновленная подзадача 2 эпика", "описание");
        updatedSub2.setStatus(TaskStatus.DONE);

        updatedSub2.setId(sub2.getId());
        manager.updateSubtask(updatedSub2);
        System.out.println("\nИзменили статус подзадачи второго эпика на DONE");
        System.out.println(manager.getEpicById(epic2.getId()));
        System.out.println("Подзадачи 2го эпика:");
        System.out.println(manager.getEpicSubtasks(epic2.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        System.out.println("\nУдаление первой подзадачи первого эпика");
        manager.deleteSubtaskById(updatedSub11.getId());
        System.out.println("1ый эпик:");
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println("Подзадачи 1го эпика:");
        System.out.println(manager.getEpicSubtasks(epic1.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());

        System.out.println("\nУдаление всех подзадач");
        manager.deleteAllSubtasks();
        System.out.println("1ый эпик:");
        System.out.println(manager.getEpicById(epic1.getId()));
        System.out.println("Подзадачи 1го эпика:");
        System.out.println(manager.getEpicSubtasks(epic1.getId()));
        System.out.println("2ой эпик:");
        System.out.println(manager.getEpicById(epic2.getId()));
        System.out.println("Подзадачи 2го эпика:");
        System.out.println(manager.getEpicSubtasks(epic2.getId()));
        System.out.println("История:");
        System.out.println(manager.getHistory());


        System.out.println("\n\n###################### Тестирование истории без дубликатов ##############################");
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        System.out.println("История после удаления всех задач: ");
        System.out.println(manager.getHistory());

        Epic epicHist1 = new Epic("1 эпик", "эпик без подзадач");
        manager.addEpic(epicHist1);
        System.out.println("Первый эпик");
        System.out.println(epicHist1);

        //Для корректоного добавлеиня эпика сначала необходимо добавить подзадачи в manager, потом их id в сам эпик
        Subtask subHist1 = new Subtask("подзадача 1", "подзадача 1");
        Subtask subHist2 = new Subtask("подзадача 2", "подзадача 2");
        Subtask subHist3 = new Subtask("подзадача 3", "подзадача 3");

        manager.addSubtask(subHist1);

        manager.addSubtask(subHist2);
        manager.addSubtask(subHist3);

        Epic epicHist2 = new Epic("2 эпик", "эпик с тремя подзадачами");
        epicHist2.addSubtaskId(subHist1.getId());
        epicHist2.addSubtaskId(subHist2.getId());
        epicHist2.addSubtaskId(subHist3.getId());
        manager.addEpic(epicHist2);
        System.out.println("\nВторой эпик");
        System.out.println(epicHist2);

        System.out.println("Подзадачи второго эпика");
        System.out.println(manager.getEpicSubtasks(epicHist2.getId()));

        System.out.println("\nИстория");
        System.out.println(manager.getHistory());

        manager.getEpicById(epicHist1.getId());
        System.out.println("\nИстория после запроса эпика без подзадач:");
        System.out.println(manager.getHistory());

        manager.getSubtaskById(subHist3.getId());
        System.out.println("\nИстория после запроса 3й подзадачи:");
        System.out.println(manager.getHistory());

        manager.deleteSubtaskById(subHist3.getId());
        System.out.println("\nИстория после удаления 3й подзадачи");
        System.out.println(manager.getHistory());

        manager.deleteEpicById(epicHist2.getId());
        System.out.println("\nИстория после удаления эпика с тремя подзадачами:");
        System.out.println(manager.getHistory());

        */


        System.out.println("\n\n###################### Тестирование FileBackedTasksManager ##############################");
        FileBackedTasksManager fbm = new FileBackedTasksManager();
        Subtask fsub1 = new Subtask("подзача 1", "описание подзадачи 1");
        Subtask fsub2 = new Subtask("подзача 2", "описание подзадачи 2");
        fbm.addSubtask(fsub1);
        fbm.addSubtask(fsub2);

        Epic fepic = new Epic("эпик", "описание эпика");
        fepic.addSubtaskId(fsub1.getId());
        fepic.addSubtaskId(fsub2.getId());
        fbm.addEpic(fepic);

        fbm.getSubtaskById(fsub1.getId());
        fbm.getEpicById(fepic.getId());

        FileBackedTasksManager fbm2 = Managers.loadFromFile(new File("tasks.csv"));
        System.out.println("\nИстория загруженного из файла менеджера");
        System.out.println(fbm2.getHistory());

        System.out.println("\nЗадачи загруженного из файла менеджера");
        System.out.println(fbm2.getEpicById(fepic.getId()));
        System.out.println(fbm2.getSubtaskById(fsub1.getId()));
        System.out.println(fbm2.getSubtaskById(fsub1.getId()));
    }
}
