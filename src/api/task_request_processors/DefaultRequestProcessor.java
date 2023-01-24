package api.task_request_processors;

import task_managers.TaskManager;
import tasks.Task;

import java.util.List;

public class DefaultRequestProcessor extends AbstractTasksRequestProcessor{

    public DefaultRequestProcessor(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public Response process(String method, String[] path, String query, String body) {
        if (method.equals("GET")) {
            List<Task> prioritized = taskManager.getPrioritizedTasks();
            return new Response(200, gson.toJson(prioritized));
        }

        return new Response(501);
    }
}
