package api.task_request_processors;

import com.google.gson.JsonSyntaxException;
import constant.HttpMethod;
import exception.TasksWithSameStartTimeException;
import task_managers.TaskManager;
import tasks.Task;

import java.util.List;
import java.util.Optional;

import static constant.HttpCode.*;

public class TaskRequestProcessor extends AbstractTasksRequestProcessor {

    public TaskRequestProcessor(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public Response process(String method, String[] path, String query, String body) {
        long id = 0;
        Optional<Long> idOpt;
        if (query != null) {
            idOpt = checkIdQuery(query);
            if (idOpt.isEmpty()) return new Response(NOT_FOUND);
            id = idOpt.get();
        }

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            return new Response(NOT_IMPLEMENTED);
        }
        switch (httpMethod) {
            case GET:
                if (query == null) return getTasks();
                else return getTaskById(id);
            case POST:
                return addTask(body);
            case DELETE:
                if (query == null) return deleteAllTasks();
                else return deleteTaskById(id);
            default:
                return new Response(NOT_IMPLEMENTED);
        }
    }

    private Response getTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        return new Response(OK, gson.toJson(tasks));
    }

    private Response getTaskById(long id) {
        Task task = taskManager.getTaskById(id);
        if (task == null) return new Response(NOT_FOUND);
        return new Response(OK, gson.toJson(task));
    }

    private Response addTask(String body) {
        Task task;
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            return new Response(BAD_REQUEST);
        }

        try {
            if (taskManager.updateTask(task) || taskManager.addTask(task)) {
                return new Response(OK, gson.toJson(task));
            }
        } catch (TasksWithSameStartTimeException e) {
            return new Response(BAD_REQUEST);
        }

        return new Response(BAD_REQUEST);
    }

    private Response deleteAllTasks() {
        taskManager.deleteAllTasks();
        return new Response(NO_CONTENT);
    }

    private Response deleteTaskById(long id) {
        taskManager.deleteTaskById(id);
        return new Response(NO_CONTENT);
    }
}
