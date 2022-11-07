package tasks;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Long> subtaskIds;

    public Epic(String title, String description) {
        super(title, description);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        return getSubtaskIds().equals(epic.getSubtaskIds());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getSubtaskIds().hashCode();
        return result;
    }

    public void addSubtaskId(long id) {
        subtaskIds.add(id);
    }

    public Set<Long> getSubtaskIds() {
        return subtaskIds;
    }
}
