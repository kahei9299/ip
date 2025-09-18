package yap.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains an ordered list of {@link Task} objects and provides operations to add, remove, access,
 * and render tasks for display.
 *
 * <p>Responsibilities: storage, 1-based access semantics, string rendering. Collaborators: {@link
 * Task} and its subclasses.
 */
public class TaskList {

  private final List<Task> tasks;

  public TaskList() {
    this.tasks = new ArrayList<>();
  }

  public TaskList(List<Task> initial) {

    assert initial != null : "Initial task list must not be null";
    for (Task t : initial) assert t != null : "Initial task list contains null task";
    this.tasks = new ArrayList<>(initial);
  }

  /**
   * Returns the number of tasks currently in the list.
   *
   * @return number of tasks
   */
  public int size() {

    assert tasks != null : "tasks list not initialized";
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
    assert index1Based >= 1 && index1Based <= size() : "1-based index out of range: " + index1Based;
    Task t = tasks.get(index1Based - 1);
    assert t != null : "Stored task is null";
    return t;
  }

  /**
   * Adds a new task to the list.
   *
   * @param t the task to add
   */
  public void add(Task t) {

    assert t != null : "Cannot add null task";
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
    assert index1Based >= 1 && index1Based <= size() : "1-based index out of range: " + index1Based;
    Task removed = tasks.remove(index1Based - 1);
    assert removed != null : "Removed task unexpectedly null";
    return removed;
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

  /**
   * Returns the 1-based indices of tasks whose names contain the given keyword (case-insensitive).
   *
   * @param keyword search term; if null or blank, returns an empty list
   * @return list of 1-based indices of matching tasks
   */
  public List<Integer> findIndices(String keyword) {
    List<Integer> out = new ArrayList<>();
    if (keyword == null) {
      return out;
    }
    final String needle = keyword.trim().toLowerCase();
    if (needle.isEmpty()) {
      return out;
    }
    for (int i = 0; i < this.size(); i++) {
      Task t = this.get(i + 1);
      if (t.getName().toLowerCase().contains(needle)) {
        out.add(i + 1); // 1-based indexing per your list semantics
      }
    }
    return out;
  }

  /**
   * Renders only the tasks whose indices are provided, using your existing toString() of each task.
   *
   * @param indices 1-based indices to render
   * @return multi-line string with numbered items
   */
  public String renderByIndices(List<Integer> indices) {
    if (indices == null || indices.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indices.size(); i++) {
      int idx = indices.get(i);
      Task t = this.get(idx); // get is 1-based in your code
      sb.append(i + 1).append('.').append(t.toString());
      if (i + 1 < indices.size()) {
        sb.append(System.lineSeparator());
      }
    }
    return sb.toString();
  }

  /**
   * Convenience method: finds matches by keyword and renders them.
   *
   * @param keyword the keyword to search for
   * @return multi-line string of matching tasks
   */
  public String renderMatches(String keyword) {
    return renderByIndices(findIndices(keyword));
  }
}
