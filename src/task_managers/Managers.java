package task_managers;

import constant.TaskStatus;
import exception.ManagerLoadException;
import task_managers.history_managers.HistoryManager;
import task_managers.history_managers.InMemoryHistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); //пропускаем строку с заголовками

            Map<Long, Task> tasks = new HashMap<>();
            Map<Long, Epic> epics = new HashMap<>();
            Map<Long, Subtask> subtasks = new HashMap<>();

            String taskStr =  br.readLine();
            while (!taskStr.isEmpty()) {
                Task task = fromString(taskStr);
                if (task instanceof Epic) {
                    epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    subtasks.put(task.getId(), (Subtask) task);
                } else {
                    tasks.put(task.getId(), task);
                }
                taskStr = br.readLine();
            }

            //добавляем id подзадач в соответсвующее эпики
            for (Subtask sub : subtasks.values()) {
                Epic epic = epics.get(sub.getEpicId());
                epic.addSubtaskId(sub.getId());
            }

            //создаем historyManager из списка истории
            List<Long> history = historyFromString(br.readLine());
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

            return new FileBackedTaskManager(historyManager,tasks,subtasks,epics);
        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось прочитать файл");
        }
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");

        long id = Long.parseLong(split[0]);
        String type = split[1];
        String title = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        Instant startTime = split[5].equals("null") ? null : Instant.parse(split[5]);
        int duration  = Integer.parseInt(split[6]);

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
        if (value == null) return new ArrayList<>();

        String[] split = value.split(",");
        List<Long> history = new ArrayList<>();
        for (int i = split.length-1; i >= 0; i--) {
            String s = split[i];
            history.add(Long.valueOf(s));
        }
        return history;
    }
}
