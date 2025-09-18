package yap.core;

import yap.io.Storage;
import yap.io.Ui;
import yap.parser.Parser;
import yap.task.Deadlines;
import yap.task.Events;
import yap.task.Task;
import yap.task.TaskList;
import yap.task.ToDos;

/**
 * Entry point of the Yap application.
 *
 * <p>Responsibilities: initialize UI, Parser, and TaskList; start the main interaction loop with
 * the user. Collaborators: interacts with Parser, TaskList, and UI.
 */
public class Yap {

  private final Storage storage;
  private TaskList tasks;
  private final Ui ui;
  private final Parser parser;

  private boolean inAddMode = false;
  private String userName = "friend";

  public Yap(String filePath) {
    ui = new Ui();
    storage = new Storage(filePath);
    parser = new Parser();
    try {
      tasks = new TaskList(storage.load());
    } catch (YapException e) {
      ui.showLoadingError();
      tasks = new TaskList();
    }
  }

  public void run() {
    ui.showWelcome();
    userName = ui.askName();
    ui.showMessage("For a list of available commands, type 'help'.");

    boolean isExit = false;
    while (!isExit) {
      ui.showLine();
      String input = ui.readCommand();
      Parser.Parsed cmd = parser.parse(input);

      try {
        switch (cmd.kind) {
          case HELP:
            ui.showMessage(helpText());
            break;

          case SHOW:
            if (tasks.size() == 0) {
              ui.showMessage("No tasks yet.");
            } else {
              ui.showMessage(tasks.render());
            }
            break;

          case ADD:
            if (!inAddMode && !"done".equalsIgnoreCase(cmd.rest)) {
              inAddMode = true;
              ui.showMessage(
                  "Entered Add mode. Use:\n"
                      + "  t <name>\n"
                      + "  d <name>/<yyyy-MM-dd>\n"
                      + "  e <name>/<yyyy-MM-dd>/<HHmm>/<HHmm>\n"
                      + "Type 'done' to exit Add mode.");
              break;
            }
            if (inAddMode) {
              if ("done".equalsIgnoreCase(cmd.rest)) {
                inAddMode = false;
                ui.showMessage("Exited Add mode.");
              } else {
                handleAddLine(cmd.rest);
                storage.save(tasks.all());
              }
            } else {
              ui.showError("Say 'add' first to enter Add mode.");
            }
            break;

          case DELETE:
            handleDelete(cmd.rest);
            storage.save(tasks.all());
            break;

          case COMPLETE:
            handleComplete(cmd.rest);
            storage.save(tasks.all());
            break;

          case EXIT:
            isExit = true;
            break;

          case FIND:
            handleFind(cmd.rest);
            break;

          case EDIT:
            handleEdit(cmd.rest);
            storage.save(tasks.all());
            break;

          case UNKNOWN:
          default:
            if (inAddMode) {
              handleAddLine(cmd.rest);
              storage.save(tasks.all());
            } else {
              ui.showError("I don't understand. Type 'help' for commands.");
            }
        }
      } catch (YapException ex) {
        ui.showError(ex.getMessage());
      }
    }
    ui.showGoodbye(userName);
  }

  private String helpText() {
    return String.join(
            "\n",
            "Commands:",
            "  show / list                  - list tasks",
            "  add                          - enter Add mode; then use t/d/e lines",
            "  delete <number|exact name>   - delete a task",
            "  complete <number|exact name> - mark a task done",
            "  edit <number|exact name> [n/NAME] [d/YYYY-MM-DD] [t/HHmm-HHmm]",
            "      Rules:",
            "        - Todo: only n/ allowed",
            "        - Deadline: n/, d/ allowed",
            "        - Event: n/, d/, and t/ allowed (t/ must be HHmm-HHmm)",
            "  find <keyword>               - list tasks whose description contains the keyword",
            "  help                         - show this help",
            "  exit / quit                  - exit the program"
    );
  }

  private void handleAddLine(String line) throws YapException {
    String trimmed = line.trim();
    if (trimmed.isEmpty()) {
      ui.showError("Empty add line.");
      return;
    }

    char kind = Character.toLowerCase(trimmed.charAt(0));
    String payload = trimmed.length() > 1 ? trimmed.substring(1).trim() : "";

    switch (kind) {
      case 't':
        if (payload.isEmpty()) throw new YapException("ToDo name is empty.");
        Task t = new ToDos(payload);
        tasks.add(t);
        ui.showMessage("Added: " + t);
        break;
      case 'd':
        String[] parts = payload.split("/", 2);
        if (parts.length != 2) throw new YapException("Deadline needs: d <name>/<yyyy-MM-dd>");
        Task d = new Deadlines(parts[0].trim(), parts[1].trim());
        tasks.add(d);
        ui.showMessage("Added: " + d);
        break;
      case 'e':
        String[] eParts = payload.split("/", 4);
        if (eParts.length != 4)
          throw new YapException("Event needs: e <name>/<yyyy-MM-dd>/<HHmm>/<HHmm>");
        Task e = new Events(eParts[0].trim(), eParts[1].trim(), eParts[2].trim(), eParts[3].trim());
        tasks.add(e);
        ui.showMessage("Added: " + e);
        break;
      default:
        throw new YapException("Unknown add-line type. Use t/d/e.");
    }
  }

  private void handleFind(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      ui.showLine();
      ui.showMessage("Please provide a keyword. Usage: find <keyword>");
      ui.showLine();
      return;
    }
    java.util.List<Integer> hits = tasks.findIndices(keyword);
    ui.showLine();
    if (hits.isEmpty()) {
      ui.showMessage("No matching tasks found.");
    } else {
      ui.showMessage("Here are the matching tasks in your list:");
      ui.showMessage(tasks.renderByIndices(hits));
    }
    ui.showLine();
  }

  private void handleDelete(String arg) throws YapException {
    if (arg.isBlank()) throw new YapException("Delete needs a number or exact task name.");
    Task removed;
    if (isInteger(arg)) {
      removed = tasks.remove(Integer.parseInt(arg));
    } else {
      int idx = indexOfExactName(arg);
      if (idx < 0) throw new YapException("Task not found: " + arg);
      removed = tasks.remove(idx + 1);
    }
    ui.showMessage("Removed: " + removed);
  }

  private void handleComplete(String arg) throws YapException {
    if (arg.isBlank()) throw new YapException("Complete needs a number or exact task name.");
    Task t;
    if (isInteger(arg)) {
      t = tasks.get(Integer.parseInt(arg));
    } else {
      int idx = indexOfExactName(arg);
      if (idx < 0) throw new YapException("Task not found: " + arg);
      t = tasks.get(idx + 1);
    }
    t.markDone();
    ui.showMessage("Marked as done: " + t);
  }

  private int indexOfExactName(String name) {
    for (int i = 0; i < tasks.size(); i++) {
      if (tasks.all().get(i).getName().equals(name)) {
        return i;
      }
    }
    return -1;
  }

  private static boolean isInteger(String s) {
    try {
      Integer.parseInt(s.trim());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void handleEdit(String rest) throws YapException {
    if (rest == null || rest.trim().isEmpty()) {
      throw new YapException("Usage: edit <index|exact name> [n/NAME] [d/YYYY-MM-DD] [t/HHmm-HHmm]");
    }

    String s = rest.trim();
    int sp = s.indexOf(' ');
    if (sp < 0) throw new YapException("No fields to change. Provide n/, d/, or t/.");

    String target = s.substring(0, sp).trim();
    String opts   = s.substring(sp + 1).trim();

    // locate task by index or exact name
    int idx0;
    if (isInteger(target)) {
      int oneBased = Integer.parseInt(target);
      if (oneBased < 1 || oneBased > tasks.size()) throw new YapException("Invalid index.");
      idx0 = oneBased - 1;
    } else {
      int byName = indexOfExactName(target);
      if (byName < 0) throw new YapException("Task not found: " + target);
      idx0 = byName;
    }

    Task original = tasks.all().get(idx0);
    EditArgs args = parseEditArgs(opts); // your existing n/, d/, t/HHmm-HHmm parser

    // mutate in place with type rules
    if (original instanceof ToDos) {
      if (args.date != null || args.timeStart != null || args.timeEnd != null) {
        throw new YapException("Todo can only change name (use n/).");
      }
      String newName = coalesce(args.name, original.getName());
      if (newName == null || newName.isBlank()) throw new YapException("Name cannot be empty.");
      original.setName(newName); // from Task.java

    } else if (original instanceof Deadlines) {
      if (args.timeStart != null || args.timeEnd != null) {
        throw new YapException("Deadline can change name and date only (n/, d/).");
      }
      Deadlines dl = (Deadlines) original;
      String newName = coalesce(args.name, dl.getName());
      String isoDate = coalesce(args.date, dl.getBy().toString());
      if (newName == null || newName.isBlank()) throw new YapException("Name cannot be empty.");
      dl.setName(newName);
      dl.setBy(isoDate);

    } else if (original instanceof Events) {
      Events ev = (Events) original;
      String newName = coalesce(args.name, ev.getName());
      String isoDate = coalesce(args.date, ev.getDate().toString());
      String startHHmm = (args.timeStart != null)
              ? args.timeStart
              : ev.getStart().format(java.time.format.DateTimeFormatter.ofPattern("HHmm"));
      String endHHmm = (args.timeEnd != null)
              ? args.timeEnd
              : ev.getEnd().format(java.time.format.DateTimeFormatter.ofPattern("HHmm"));
      if (newName == null || newName.isBlank()) throw new YapException("Name cannot be empty.");
      if (startHHmm.compareTo(endHHmm) >= 0) throw new YapException("Start must be before end.");
      ev.setName(newName);
      ev.setDate(isoDate);
      ev.setStart(startHHmm);
      ev.setEnd(endHHmm);

    } else {
      throw new YapException("Unsupported task type for edit.");
    }

    // nudge observers without creating a new object
    tasks.all().set(idx0, original);

    ui.showMessage("Edited: " + original.toString());
  }


  // Small holder for parsed args
  private static final class EditArgs {
    String name;        // from n/
    String date;        // ISO yyyy-MM-dd from d/
    String timeStart;   // HHmm from t/ start
    String timeEnd;     // HHmm from t/ end
  }

  // Mini parser for n/, d/, t/HHmm-HHmm (values may contain spaces for n/)
  private static EditArgs parseEditArgs(String s) throws YapException {
    EditArgs a = new EditArgs();
    if (s == null || s.isBlank()) return a;

    // Find prefix positions
    int nPos = indexOfWordPref(s, "n/");
    int dPos = indexOfWordPref(s, "d/");
    int tPos = indexOfWordPref(s, "t/");

    // Extract substring for each prefix up to the next prefix
    a.name = sliceVal(s, nPos, dPos, tPos);
    a.date = sliceVal(s, dPos, nPos, tPos);

    String tVal = sliceVal(s, tPos, nPos, dPos);
    if (tVal != null && !tVal.isBlank()) {
      String v = tVal.trim();
      // accept "HHmm-HHmm" or a single "HHmm" (will be treated as both start & end invalid; better to require dash)
      String[] parts = v.split("-");
      if (parts.length != 2) throw new YapException("Time uses t/HHmm-HHmm (e.g., t/0900-1030).");
      a.timeStart = parts[0].trim();
      a.timeEnd   = parts[1].trim();
      if (!a.timeStart.matches("\\d{4}") || !a.timeEnd.matches("\\d{4}")) {
        throw new YapException("Time must be 4 digits (HHmm).");
      }
    }

    return a;
  }

  // helpers for prefix parsing
  private static int indexOfWordPref(String s, String pref) {
    // match at start or after space
    int i = s.indexOf(" " + pref);
    int j = (s.startsWith(pref) ? 0 : -1);
    if (j == 0) return 0;
    return i < 0 ? -1 : i + 1;
  }

  private static String sliceVal(String s, int selfPos, int other1Pos, int other2Pos) {
    if (selfPos < 0) return null;
    int end = s.length();
    if (other1Pos >= 0 && other1Pos > selfPos) end = Math.min(end, other1Pos);
    if (other2Pos >= 0 && other2Pos > selfPos) end = Math.min(end, other2Pos);
    String raw = s.substring(selfPos + 2, end).trim(); // skip "x/"
    return raw.isEmpty() ? null : raw;
  }

  private static <T> T coalesce(T a, T b) { return (a != null) ? a : b; }

  /**
   * Launches the Yap application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    new Yap("data/tasks.txt").run();
  }
}
