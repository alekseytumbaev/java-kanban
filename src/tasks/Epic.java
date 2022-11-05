package tasks;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Long> subtaskIds;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        subtaskIds = new HashSet<>();
    }

    public void addSubtaskId(long id) {
        subtaskIds.add(id);
    }

    public Set<Long> getSubtaskIds() {
        return subtaskIds;
    }
}
