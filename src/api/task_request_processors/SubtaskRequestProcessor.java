package api.task_request_processors;

import com.google.gson.JsonSyntaxException;
import exception.TasksWithSameStartTimeException;
import task_managers.TaskManager;
import tasks.Subtask;

import java.util.List;
import java.util.Optional;

public class SubtaskRequestProcessor extends AbstractTasksRequestProcessor {

    public SubtaskRequestProcessor(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public Response process(String method, String[] path, String query, String body) {
        long id = 0;
        Optional<Long> idOpt;
        if (query != null) {
            idOpt = checkIdQuery(query);
            if (idOpt.isEmpty()) return new Response(400);
            id = idOpt.get();
        }

        switch (method) {
            case "GET":
                if (query == null) return getSubtasks();
                else if (path.length == 4) return getEpicSubtasks(id);
                else return getSubtaskById(id);
            case "POST":
                return addSubtask(body);
            case "DELETE":
                if (query == null) return deleteAllSubtasks();
                else return deleteSubtaskById(id);
            default:
                return new Response(501);
        }
    }

    private Response getSubtasks() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        return new Response(200, gson.toJson(subtasks));
    }

    private Response getSubtaskById(long id) {
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask == null) return new Response(404);
        return new Response(200, gson.toJson(subtask));
    }

    private Response addSubtask(String body) {
        Subtask subtask;
        try {
            subtask = gson.fromJson(body, Subtask.class);
        } catch (JsonSyntaxException e) {
            return new Response(400);
        }

        try {
            if (taskManager.updateSubtask(subtask) || taskManager.addSubtask(subtask)) {
                return new Response(200, gson.toJson(subtask));
            }
        } catch (TasksWithSameStartTimeException e) {
            return new Response(400);
        }
        return new Response(400);
    }

    private Response deleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        return new Response(204);
    }

    private Response deleteSubtaskById(long id) {
        taskManager.deleteSubtaskById(id);
        return new Response(204);
    }

    private Response getEpicSubtasks(long id) {
        List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
        if (subtasks == null) return new Response(404);
        return new Response(200, gson.toJson(subtasks));
    }
}