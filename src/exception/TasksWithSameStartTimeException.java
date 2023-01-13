package exception;

public class TasksWithSameStartTimeException extends RuntimeException {
    public TasksWithSameStartTimeException(String message) {
        super(message);
    }
}
