package tasks;

import constant.TaskStatus;

import java.time.Instant;

public class Subtask extends Task {
    private long epicId;

    public Subtask(String title, String description, Instant timeStart, int duration) {
        super(title, description, timeStart, duration);
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    public Subtask(long id, String title, String description, TaskStatus status, long epicId,
                   Instant timeStart, int duration) {
        super(id, title, description, status, timeStart, duration);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        if (!super.equals(o)) return false;

        Subtask subtask = (Subtask) o;

        return getEpicId() == subtask.getEpicId();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (getEpicId() ^ (getEpicId() >>> 32));
        return result;
    }
}
