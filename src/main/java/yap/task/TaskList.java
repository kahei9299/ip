package yap.task;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
  private final List<Task> tasks;

  public TaskList() {
    this.tasks = new ArrayList<>();
  }

  public TaskList(List<Task> initial) {
    this.tasks = new ArrayList<>(initial);
  }

  /** Returns the number of tasks in the list. */
  public int size() {
    return tasks.size();
  }

  /**
   * Returns the task at a 1-based index.
   *
   * @param index1Based index starting at 1
   * @return the task at that position
   */
  public Task get(int index1Based) {
    return tasks.get(index1Based - 1);
  }

  /** Adds a new task to the end of the list. */
  public void add(Task t) {
    tasks.add(t);
  }

  /**
   * Removes and returns the task at a 1-based index.
   *
   * @param index1Based index starting at 1
   * @return the removed task
   */
  public Task remove(int index1Based) {
    return tasks.remove(index1Based - 1);
  }

  public List<Task> all() {
    return new ArrayList<>(tasks);
  }

  /** Renders the list as a user-facing multi-line string. */
  public String render() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tasks.size(); i++) {
      sb.append(i + 1).append(". ").append(tasks.get(i).toString());
      if (i + 1 < tasks.size()) sb.append(System.lineSeparator());
    }
    return sb.toString();
  }
}
