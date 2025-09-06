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
}
