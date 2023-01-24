package api.task_request_processors;

import com.google.gson.Gson;
import task_managers.TaskManager;

import java.util.Optional;

public abstract class AbstractTasksRequestProcessor {

    protected Gson gson;
    protected TaskManager taskManager;

    public AbstractTasksRequestProcessor(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new Gson();
    }

    abstract public Response process(String method, String[] path, String query, String body);

    protected Optional<Long> checkIdQuery(String idQuery) {
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
}
