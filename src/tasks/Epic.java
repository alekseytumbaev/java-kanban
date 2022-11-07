package tasks;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Long> subtaskIds;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        subtaskIds = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void addSubtaskId(long id) {
        subtaskIds.add(id);
    }

    public Set<Long> getSubtaskIds() {
        return subtaskIds;
    }
}
