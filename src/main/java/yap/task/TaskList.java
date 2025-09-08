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

    public int size() { return tasks.size(); }

    public Task get(int index1Based) {
        return tasks.get(index1Based - 1);
    }

    public void add(Task t) {
        tasks.add(t);
    }

    public Task remove(int index1Based) {
        return tasks.remove(index1Based - 1);
    }

    public List<Task> all() {
        return new ArrayList<>(tasks);
    }

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
    public java.util.List<Integer> findIndices(String keyword) {
        java.util.List<Integer> out = new java.util.ArrayList<>();
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
    public String renderByIndices(java.util.List<Integer> indices) {
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

    public String renderMatches(String keyword) {
        return renderByIndices(findIndices(keyword));
    }

}
