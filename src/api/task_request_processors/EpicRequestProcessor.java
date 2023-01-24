package api.task_request_processors;

import com.google.gson.JsonSyntaxException;
import exception.TasksWithSameStartTimeException;
import task_managers.TaskManager;
import tasks.Epic;

import java.util.List;
import java.util.Optional;

public class EpicRequestProcessor extends AbstractTasksRequestProcessor {


    public EpicRequestProcessor(TaskManager taskManager) {
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
                if (query == null) return getEpics();
                else return getEpicById(id);
            case "POST":
                return addEpic(body);
            case "DELETE":
                if (query == null) return deleteAllEpics();
                else return deleteEpicById(id);
            default:
                return new Response(501);
        }
    }

    private Response getEpics() {
        List<Epic> epics = taskManager.getAllEpics();
        return new Response(200, gson.toJson(epics));
    }

    private Response getEpicById(long id) {
        Epic epic = taskManager.getEpicById(id);
        if (epic == null) return new Response(404);
        return new Response(200, gson.toJson(epic));
    }

    private Response addEpic(String body) {
        Epic epic;
        try {
            epic = gson.fromJson(body, Epic.class);
        } catch (JsonSyntaxException e) {
            return new Response(400);
        }

        try {
            if (taskManager.updateEpic(epic) || taskManager.addEpic(epic)) {
                return new Response(200, gson.toJson(epic));
            }
        } catch (TasksWithSameStartTimeException e) {
            return new Response(400);
        }

        return new Response(400);
    }

    private Response deleteAllEpics() {
        taskManager.deleteAllEpics();
        return new Response(204);
    }

    private Response deleteEpicById(long id) {
        taskManager.deleteEpicById(id);
        return new Response(204);
    }
}
