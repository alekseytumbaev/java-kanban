package tasks;

import constant.TaskStatus;

import java.time.Instant;

public class Task {
    protected long id;
    protected String title;
    protected String description;
    protected TaskStatus status;
    protected Instant startTime;
    protected int duration;

    public Task(long id, String title, String description, TaskStatus status, Instant startTime, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, Instant startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
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

        Task task = (Task) o;

        if (getId() != task.getId()) return false;
        if (!getTitle().equals(task.getTitle())) return false;
        if (!getDescription().equals(task.getDescription())) return false;
        return getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getStatus().hashCode();
        return result;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus status) {
        if (status == null) return;

        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public Instant getEndTime() {
        return startTime.plusSeconds(duration * 60L);
    }
}