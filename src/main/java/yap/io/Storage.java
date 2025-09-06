package yap.io;

import yap.core.YapException;
import yap.task.Task;
import yap.task.ToDos;
import yap.task.Deadlines;
import yap.task.Events;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Saves and loads tasks from "./data/yap.txt".
 * Format (human-friendly, stable to parse):
 *   T | 1 | read book
 *   D | 0 | return book | 2019-12-02
 *   E | 0 | project meeting | 2019-12-02 | 1800 | 2000
 * Notes:
 *  - Relative, OS-independent path.
 *  - Creates parent directory if needed.
 *  - Skips blank lines, comment lines (#...), and corrupted lines.
 */
public final class Storage {

    private static final DateTimeFormatter TIME_IO = DateTimeFormatter.ofPattern("HHmm");
    private final Path file;

    public Storage(String relativePath) {
        this.file = Paths.get(relativePath);
    }

    /** Loads tasks from disk. Returns empty list if file missing. */
    public List<Task> load() throws YapException {
        List<Task> out = new ArrayList<>();
        try {
            if (!Files.exists(file)) {
                return out; // first run, nothing to load
            }
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String raw = line.trim();
                if (raw.isEmpty() || raw.startsWith("#")) {
                    continue;
                }
                Task t = deserialize(raw);
                if (t != null) {
                    out.add(t); // corrupted lines are skipped
                }
            }
        } catch (IOException ex) {
            throw new YapException("Failed to read save file: " + file, ex);
        }
        return out;
    }

    /** Saves tasks to disk atomically. Creates parent directory if necessary. */
    public void save(List<Task> tasks) throws YapException {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path tmp = (parent == null ? Paths.get(".") : parent).resolve(file.getFileName() + ".tmp");

            try (BufferedWriter w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                for (Task t : tasks) {
                    w.write(serialize(t));
                    w.newLine();
                }
            }

            try {
                Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new YapException("Failed to write save file: " + file, ex);
        }
    }

    // ----------------------------------------------------------------------
    // Serialization
    // ----------------------------------------------------------------------

    private static String serialize(Task t) {
        String status = t.getStatus() ? "1" : "0";

        if (t instanceof ToDos) {
            return join("T", status, t.getName());

        } else if (t instanceof Deadlines) {
            Deadlines d = (Deadlines) t;
            return join("D", status, d.getName(), d.getBy().toString());

        } else if (t instanceof Events) {
            Events e = (Events) t;
            String dateIso = e.getDate().toString();
            String startHHmm = e.getStart().format(TIME_IO);
            String endHHmm = e.getEnd().format(TIME_IO);
            return join("E", status, e.getName(), dateIso, startHHmm, endHHmm);
        }

        return join("?", status, t.getName());
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
                        t.setStatus(true);
                    }
                    return t;
                }
                case "D": {
                    if (parts.length < 4) {
                        return null;
                    }
                    Deadlines d = new Deadlines(parts[2], parts[3]);
                    if ("1".equals(status)) {
                        d.setStatus(true);
                    }
                    return d;
                }
                case "E": {
                    if (parts.length < 6) {
                        return null;
                    }
                    Events e = new Events(parts[2], parts[3], parts[4], parts[5]);
                    if ("1".equals(status)) {
                        e.setStatus(true);
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
