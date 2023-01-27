package api.task_request_processors;

import constant.HttpCode;
import constant.HttpMethod;
import task_managers.TaskManager;
import tasks.Task;

import java.util.List;

public class DefaultRequestProcessor extends AbstractTasksRequestProcessor{

    public DefaultRequestProcessor(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public Response process(String method, String[] path, String query, String body) {
        if (method.equals(HttpMethod.GET.toString())) {
            List<Task> prioritized = taskManager.getPrioritizedTasks();
            return new Response(HttpCode.OK, gson.toJson(prioritized));
        }

        return new Response(HttpCode.NOT_IMPLEMENTED);
    }
}
