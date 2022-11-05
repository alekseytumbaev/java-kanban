package tasks;

public class Subtask extends Task {
    private long epicId;

    public Subtask(String title, String description, TaskStatus status) {
        super(title, description, status);
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }
}
