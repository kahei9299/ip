package yap.task;

/**
 * Abstract base class representing a generic Task.
 * <p>
 * Responsibilities: store task name and completion state,
 * and provide core behaviours common to all tasks.
 * Collaborators: extended by ToDos, Deadlines, and Events.
 */

public class Task {

    private String name;
    private Boolean isdone;

    public Task(String name){
        this.name = name;
        this.isdone = false;
    }

    /**
     * Returns the name/description of the task.
     *
     * @return the task name
     */
    public String getName() {
        return this.name;
    }
    public String getStatusIcon() {
        return isdone ? "X" : " ";
    }

    public void setStatus(Boolean isdone) {
        this.isdone = isdone;
    }

    public Boolean getStatus() {
        return isdone;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isdone = true;
    }
    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.name);
    }
}
