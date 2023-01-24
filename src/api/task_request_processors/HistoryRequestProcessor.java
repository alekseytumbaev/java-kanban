package api.task_request_processors;

import task_managers.TaskManager;
import tasks.Task;

import java.util.List;

public class HistoryRequestProcessor extends AbstractTasksRequestProcessor {

    public HistoryRequestProcessor(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public Response process(String method, String[] path, String query, String body) {
        if (method.equals("GET")) {
            List<Task> history = taskManager.getHistory();
            return new Response(200, gson.toJson(history));
        }

        return new Response(501);
    }
}
