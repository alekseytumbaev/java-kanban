package api.task_request_processors;

import task_managers.TaskManager;
import tasks.Task;

import java.util.List;

import static constant.HttpCode.NOT_IMPLEMENTED;
import static constant.HttpCode.OK;
import static constant.HttpMethod.GET;

public class HistoryRequestProcessor extends AbstractTasksRequestProcessor {

    public HistoryRequestProcessor(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public Response process(String method, String[] path, String query, String body) {
        if (method.equals(GET.toString())) {
            List<Task> history = taskManager.getHistory();
            return new Response(OK, gson.toJson(history));
        }

        return new Response(NOT_IMPLEMENTED);
    }
}
