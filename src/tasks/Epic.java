package tasks;

import constant.TaskStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Long> subtaskIds;
    private Instant endTime;

    public Epic(String title, String description) {
        super(title, description, null, 0);
        subtaskIds = new HashSet<>();
    }

    public Epic(long id, String title, String description, TaskStatus status,
                Instant startTime, int duration, Instant endTime) {
        super(id, title, description, status, startTime, duration);
        this.endTime = endTime;
        this.subtaskIds = new HashSet<>();
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

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    public void addSubtaskId(long id) {
        subtaskIds.add(id);
    }

    public Set<Long> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
