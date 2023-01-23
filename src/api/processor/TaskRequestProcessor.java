package api.processor;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exception.TasksWithSameStartTimeException;
import task_managers.TaskManager;
import tasks.Task;

import java.util.List;
import java.util.Optional;

public class TaskRequestProcessor {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskRequestProcessor(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new Gson();
    }

    public Response process(String method, String query, String body) {
        long id = 0;
        Optional<Long> idOpt;
        if (query != null) {
            idOpt = checkIdQuery(query);
            if (idOpt.isEmpty()) return new Response(400);
            id = idOpt.get();
        }

        switch (method) {
            case "GET":
                if (query == null) return getTasks();
                else return getTaskById(id);
            case "POST":
                return addTask(body);
            case "DELETE":
                if (query == null) return deleteAllTasks();
                else return deleteTaskById(id);
            default:
                return new Response(501);
        }
    }

    private Optional<Long> checkIdQuery(String idQuery) {
        idQuery = idQuery.replace("id=", "");
        long idLong;
        try {
            idLong = Long.parseLong((idQuery));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        if (idLong < 0) return Optional.empty();

        return Optional.of(idLong);
    }

    private Response getTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        return new Response(200, gson.toJson(tasks));
    }

    private Response getTaskById(long id) {
        Task task = taskManager.getTaskById(id);
        if (task == null) return new Response(404);
        return new Response(200, gson.toJson(task));
    }

    private Response addTask(String body) {
        Task task;
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            return new Response(400);
        }

        try {
            if (taskManager.updateTask(task) || taskManager.addTask(task)) {
                return new Response(200, gson.toJson(task));
            }
        } catch (TasksWithSameStartTimeException e) {
            return new Response(400);
        }

        return new Response(400);
    }

    private Response deleteAllTasks() {
        taskManager.deleteAllTasks();
        return new Response(204);
    }

    private Response deleteTaskById(long id) {
        taskManager.deleteTaskById(id);
        return new Response(204);
    }

}
