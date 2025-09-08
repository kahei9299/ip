package yap.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains an ordered list of {@link Task} objects and provides
 * operations to add, remove, access, and render tasks for display.
 * <p>
 * Responsibilities: storage, 1-based access semantics, string rendering.
 * Collaborators: {@link Task} and its subclasses.
 */

public class TaskList {
  
  private final List<Task> tasks;

  public TaskList() {
    this.tasks = new ArrayList<>();
  }

  public TaskList(List<Task> initial) {
    this.tasks = new ArrayList<>(initial);
  }

   /**
     * Returns the number of tasks currently in the list.
     *
     * @return number of tasks
     */
  public int size() {
    return tasks.size();
  }

  /**
     * Returns the task at the given 1-based index.
     *
     * @param index1Based the index starting at 1
     * @return the task at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
  public Task get(int index1Based) {
    return tasks.get(index1Based - 1);
  }

   /**
     * Adds a new task to the list.
     *
     * @param t the task to add
     */
  public void add(Task t) {
    tasks.add(t);
  }

   /**
     * Removes the task at the given 1-based index.
     *
     * @param index1Based the index of the task to remove
     * @return the removed task
     * @throws IndexOutOfBoundsException if the index is invalid
     */
  public Task remove(int index1Based) {
    return tasks.remove(index1Based - 1);
  }

  public List<Task> all() {
    return new ArrayList<>(tasks);
  }

   /**
     * Returns a string rendering of all tasks for display.
     *
     * @return a multi-line string of tasks
     */
  public String render() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tasks.size(); i++) {
      sb.append(i + 1).append(". ").append(tasks.get(i).toString());
      if (i + 1 < tasks.size()) sb.append(System.lineSeparator());
    }
    return sb.toString();
  }
  
}
