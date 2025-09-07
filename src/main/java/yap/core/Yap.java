package yap.core;

import yap.io.Storage;
import yap.io.Ui;
import yap.parser.Parser;
import yap.task.Deadlines;
import yap.task.Events;
import yap.task.Task;
import yap.task.TaskList;
import yap.task.ToDos;

/** Application entry point wiring UI, parser, and task list. */
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
        "  help                         - show this help",
        "  exit / quit                  - exit the program");
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
        {
          if (payload.isEmpty()) throw new YapException("ToDo name is empty.");
          Task t = new ToDos(payload);
          tasks.add(t);
          ui.showMessage("Added: " + t);
          break;
        }
      case 'd':
        {
          String[] parts = payload.split("/", 2);
          if (parts.length != 2) throw new YapException("Deadline needs: d <name>/<yyyy-MM-dd>");
          Task d = new Deadlines(parts[0].trim(), parts[1].trim());
          tasks.add(d);
          ui.showMessage("Added: " + d);
          break;
        }
      case 'e':
        {
          String[] parts = payload.split("/", 4);
          if (parts.length != 4)
            throw new YapException("Event needs: e <name>/<yyyy-MM-dd>/<HHmm>/<HHmm>");
          Task e = new Events(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
          tasks.add(e);
          ui.showMessage("Added: " + e);
          break;
        }
      default:
        throw new YapException("Unknown add-line type. Use t/d/e.");
    }
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

  /**
   * Launches the Yap application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    new Yap("data/tasks.txt").run();
  }
}
