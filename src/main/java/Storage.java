import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Storage {

    private final Path file;

    /**
     * Constructs a Storage object with a relative path
     *
     * @param relativePath relative path to the data file
     */
    public Storage(String relativePath) {
        this.file = Paths.get(relativePath);
    }

    /**
     * Loads tasks from disk. If the file does not exist yet (first run), returns an empty list.
     *
     * @return list of tasks loaded from disk (never {@code null})
     */
    public List<Task> load() {
        List<Task> out = new ArrayList<>();
        try {
            if (!Files.exists(file)) {
                return out;
            }
            for (String line : Files.readAllLines(file)) {
                String raw = line.trim();
                if (raw.isEmpty() || raw.startsWith("#")) {
                    continue;
                }
                Task task = deserialize(raw);
                if (task != null) {
                    out.add(task);
                }
            }
        } catch (IOException ex) {
            System.err.println("Warning: failed to read " + file + ": " + ex.getMessage());
        }
        return out;
    }

    /**
     * Saves all tasks to disk. Creates the parent directory if it does not exist.
     *
     * @param tasks tasks to save
     */
    public void save(List<Task> tasks) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                for (Task t : tasks) {
                    writer.write(serialize(t));
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            System.err.println("Warning: failed to save to " + file + ": " + ex.getMessage());
        }
    }

    private static String serialize(Task t) {
        String status = t.isDone() ? "F" : "U";
        if (t instanceof ToDos) {
            return join("T", status, t.getName());
        } else if (t instanceof Deadlines) {
            Deadlines d = (Deadlines) t;
            return join("D", status, d.getName(), d.getDeadline());
        } else if (t instanceof Events) {
            Events e = (Events) t;
            return join("E", status, e.getName(), e.getDate(), e.getStart(), e.getEnd());
        } else {
            return join("?", status, t.getName());
        }
    }

    private static Task deserialize(String line) {
        String[] parts = Arrays.stream(line.split("\\|"))
                .map(String::trim)
                .toArray(String[]::new);
        if (parts.length < 3) {
            return null;
        }
        String type = parts[0];
        String status = parts[1];
        try {
            switch (type) {
                case "T": {
                    ToDos t = new ToDos(parts[2]);
                    if ("1".equals(status)) {
                        t.markDone();
                    }
                    return t;
                }
                case "D": {
                    if (parts.length < 4) {
                        return null;
                    }
                    Deadlines d = new Deadlines(parts[2], parts[3]);
                    if ("1".equals(status)) {
                        d.markDone();
                    }
                    return d;
                }
                case "E": {
                    if (parts.length < 6) {
                        return null;
                    }
                    Events e = new Events(parts[2], parts[3], parts[4], parts[5]);
                    if ("1".equals(status)) {
                        e.markDone();
                    }
                    return e;
                }
                default:
                    return null;
            }
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static String join(String a, String b, String c) {
        return a + " | " + b + " | " + c;
    }

    private static String join(String a, String b, String c, String d) {
        return a + " | " + b + " | " + c + " | " + d;
    }

    private static String join(String a, String b, String c, String d, String e, String f) {
        return a + " | " + b + " | " + c + " | " + d + " | " + e + " | " + f;
    }
}
