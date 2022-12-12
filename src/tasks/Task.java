package tasks;

import constant.TaskStatus;

public class Task {
    protected long id;
    protected String title;
    protected String description;
    protected TaskStatus status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
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

}