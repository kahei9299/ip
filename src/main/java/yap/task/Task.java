package yap.task;

public class Task {

  private String name;
  private Boolean isDone;

  /** Creates a new task with the given name; tasks start as not done. */
  public Task(String name) {
    this.name = name;
    this.isDone = false;
  }

  /** Returns the user-facing name/description of this task. */
  public String getName() {
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

  /** Marks this task as done. */
  public void markDone() {
    this.isDone = true;
  }

  @Override
  public String toString() {
    return String.format("[%s] %s", this.getStatusIcon(), this.name);
  }
}
