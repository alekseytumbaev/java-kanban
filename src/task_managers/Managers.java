package task_managers;

import constant.Endpoint;
import constant.Port;
import constant.TaskStatus;
import exception.ManagerLoadException;
import task_managers.history_managers.HistoryManager;
import task_managers.history_managers.InMemoryHistoryManager;
import api.kv.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Managers {
    public static TaskManager getDefault() {
        return loadFromKVServer(Endpoint.HOST.url + Port.KV_SERVER.port, "key");
    }

    public static HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskManager loadFromKVServer(String serverAddress, String key) {
        KVTaskClient client = new KVTaskClient(serverAddress);
        String taskManagerStr = client.load(key);
        if (taskManagerStr == null) return new HttpTaskManager(client, key);

        TaskManagerInfoCarrier carrier = deserialize(taskManagerStr);
        return new HttpTaskManager(
                carrier.currentId,
                carrier.historyManager,
                carrier.tasks, carrier.subtasks, carrier.epics,
                client, key);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось прочитать файл");
        }
        TaskManagerInfoCarrier carrier = deserialize(content);
        return new FileBackedTaskManager(
                carrier.currentId,
                carrier.historyManager,
                carrier.tasks,
                carrier.subtasks,
                carrier.epics);
    }

    private static TaskManagerInfoCarrier deserialize(String taskManager) {
        String[] arr = taskManager.split(System.lineSeparator());

        long currId = Long.parseLong(arr[0]);
        Map<Long, Task> tasks = new HashMap<>();
        Map<Long, Epic> epics = new HashMap<>();
        Map<Long, Subtask> subtasks = new HashMap<>();

        int i = 2;
        String taskStr = arr.length > i ? arr[i] : "";
        while (!taskStr.isEmpty()) {
            Task task = fromString(taskStr);
            if (task instanceof Epic) {
                epics.put(task.getId(), (Epic) task);
            } else if (task instanceof Subtask) {
                subtasks.put(task.getId(), (Subtask) task);
            } else {
                tasks.put(task.getId(), task);
            }

            i++;
            taskStr = arr.length > i ? arr[i] : "";
        }

        //добавляем id подзадач в соответсвующее эпики
        for (Subtask sub : subtasks.values()) {
            Epic epic = epics.get(sub.getEpicId());
            epic.addSubtaskId(sub.getId());
        }

        //создаем historyManager из списка истории
        i++;
        List<Long> history = historyFromString(arr.length > i ? arr[i] : "");
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (Long id : history) {
            Task task;
            if (tasks.containsKey(id)) {
                task = tasks.get(id);
            } else if (epics.containsKey(id)) {
                task = epics.get(id);
            } else if (subtasks.containsKey(id)) {
                task = subtasks.get(id);
            } else {
                throw new ManagerLoadException("Повторяющиеся Id в файле");
            }
            historyManager.add(task);
        }
        return new TaskManagerInfoCarrier(currId, tasks, epics, subtasks, historyManager);
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");

        long id = Long.parseLong(split[0]);
        String type = split[1];
        String title = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        Instant startTime = split[5].equals("null") ? null : Instant.parse(split[5]);
        int duration = Integer.parseInt(split[6]);

        Task task;
        if (type.equals(Subtask.class.getSimpleName().toUpperCase())) {
            long epic = Long.parseLong(split[7]);
            task = new Subtask(id, title, description, status, epic, startTime, duration);
        } else if (type.equals(Epic.class.getSimpleName().toUpperCase())) {
            Instant endTime = split[7].equals("null") ? null : Instant.parse(split[7]);
            task = new Epic(id, title, description, status, startTime, duration, endTime);
        } else {
            task = new Task(id, title, description, status, startTime, duration);
        }
        return task;
    }

    private static List<Long> historyFromString(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();

        String[] split = value.split(",");
        List<Long> history = new ArrayList<>();
        for (int i = split.length - 1; i >= 0; i--) {
            String s = split[i];
            history.add(Long.valueOf(s));
        }
        return history;
    }

    private static class TaskManagerInfoCarrier {

        public TaskManagerInfoCarrier(
                long currentId,
                Map<Long, Task> tasks, Map<Long, Epic> epics, Map<Long, Subtask> subtasks,
                HistoryManager historyManager) {
            this.currentId = currentId;
            this.tasks = tasks;
            this.epics = epics;
            this.subtasks = subtasks;
            this.historyManager = historyManager;
        }

        long currentId;
        Map<Long, Task> tasks;
        Map<Long, Epic> epics;
        Map<Long, Subtask> subtasks;
        HistoryManager historyManager;
    }
}
