package yap.task;

/**
 * Abstract base class representing a generic Task.
 *
 * <p>Responsibilities: store task name and completion state, and provide core behaviours common to
 * all tasks. Collaborators: extended by ToDos, Deadlines, and Events.
 */
public class Task {

  private final String name;
  private boolean isDone;

  /** Creates a new task with the given name; tasks start as not done. */
  public Task(String name) {

    assert name != null && !name.isBlank() : "Task name must be non-null, non-blank";
    this.name = name;
    this.isDone = false;
  }

  /**
   * Returns the name/description of the task.
   *
   * @return the task name
   */
  public String getName() {

    assert this.name != null : "Task name unexpectedly null";
    return this.name;
  }

  /** Returns "X" if done, otherwise a single space. */
  public String getStatusIcon() {

    return isDone ? "X" : " ";
  }

  public void setStatus(Boolean isdone) {
    this.isDone = isdone;
  }

  public Boolean getStatus() {
    return isDone;
  }

  /** Returns true if the task has been marked done. */
  public boolean isDone() {
    return Boolean.TRUE.equals(isDone);
  }

  /** Marks this task as completed. */
  public void markDone() {

    this.isDone = true;
  }

  @Override
  public String toString() {
    return String.format("[%s] %s", this.getStatusIcon(), this.name);
  }
}
