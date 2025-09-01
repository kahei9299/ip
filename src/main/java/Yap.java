import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.NoSuchElementException;

public class Yap {

    private static final Scanner sc = new Scanner(System.in);
    private static final Pattern NO = Pattern.compile("\\b(no|n|nah|nope)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern YES = Pattern.compile("\\b(yes|y|yeah|yup|sure)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern CMD_SHOW  = Pattern.compile("^show(?:\\s+(\\d+))?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CMD_HELP  = Pattern.compile("^help$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CMD_EXIT  = Pattern.compile("^(exit|quit)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CMD_ADD  = Pattern.compile("^(add|todo)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CMD_DONE  = Pattern.compile("^(done)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CMD_COMPLETE = Pattern.compile("^complete\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CMD_DELETE = Pattern.compile("^delete\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    private final List<Task> tasks = new ArrayList<>();
    private final Storage storage = new Storage("data/duke.txt");

    public static void main(String[] args) {
        new Yap().run();
    }

    private String ask(String prompt) {
        while (true) {
            String s = read(prompt + " ");
            if (!s.isBlank()) return s.trim();
            System.out.println("Please enter something.");
        }
    }

    private String read(String prompt) {
        try {
            System.out.print(prompt);
            String s = sc.nextLine();
            if (s == null || s.isBlank()) {
                throw new IllegalArgumentException("Input cannot be empty.");
            }
            return s.trim();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return read(prompt);
        }
    }

    public void run() {

        tasks.addAll(storage.load());

        System.out.printf("Hello! I'm Yap your new best friend!\n");
        String name = ask("May I know what's your name?");
        System.out.printf("Hi %s, how may i help you today? :D\n", name);
        System.out.println("for a list of available commands, type 'help' ");

        boolean addingTasks = false;

        while (true) {
            String input = read("> ");

            if (addingTasks) {
                if (CMD_DONE.matcher(input).matches()) {
                    addingTasks = false;
                    System.out.println("Finished adding tasks.");
                } else {
                    try {
                        String cleanedInput = input.trim();
                        char firstletter = cleanedInput.toLowerCase().charAt(0);
                        TaskType taskType = TaskType.fromChar(firstletter);

                        switch (taskType) {
                            case TODO:
                                if (cleanedInput.charAt(1) != ' ') {
                                    throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
                                }
                                String taskDetails = cleanedInput.substring(1).trim();
                                tasks.add(new ToDos(taskDetails));
                                storage.save(tasks);
                                System.out.println("Added ToDo task: " + taskDetails);
                                break;
                            case DEADLINE:
                                if (cleanedInput.charAt(1) != ' ') {
                                    throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
                                }
                                String deadlinetaskDetails = cleanedInput.substring(1).trim();
                                String[] deadlineparts = deadlinetaskDetails.split("/");
                                tasks.add(new Deadlines(deadlineparts[0], deadlineparts[1]));
                                storage.save(tasks);
                                System.out.println(("Added Deadline task: " + deadlineparts[0]));
                                break;
                            case EVENT:
                                if (cleanedInput.charAt(1) != ' ') {
                                    throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
                                }
                                String eventtaskDetails = cleanedInput.substring(1).trim();
                                String[] eventparts = eventtaskDetails.split("/");
                                tasks.add(new Events(eventparts[0], eventparts[1], eventparts[2], eventparts[3]));
                                storage.save(tasks);
                                System.out.println("Added Event: " + eventparts[0]);
                                break;

                        }
                    } catch (InvalidTaskTypeException e) {
                        System.out.println(e.getMessage());
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid input format. Please provide a task with the correct format.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Missing task details. Please ensure all required fields are provided.");
                    }
                }
                continue;
            }

            String command = input.split(" ")[0];
            CommandType commandType = CommandType.fromString(command);
            if (CMD_ADD.matcher(input).matches()) {
                addingTasks = true;
                System.out.println("Add mode: enter tasks one per line. Type 'done' when finished.");
                continue;
            }

            if (CMD_EXIT.matcher(input).matches()) {
                System.out.printf("Alright, it was nice talking to you %s! See you next time :D%n", name);
                break;
            }

            var mShow = CMD_SHOW.matcher(input);
            if (mShow.matches()) {
                Integer n = mShow.group(1) == null ? null : Integer.parseInt(mShow.group(1));
                show(n);
                continue;
            }

            if (CMD_HELP.matcher(input).matches()) {
                help();
                continue;
            }

            var mComplete = CMD_COMPLETE.matcher(input);
            if(mComplete.matches()) {
                String s = mComplete.group(1).trim();
                complete(s);
                storage.save(tasks);
                continue;
            }

            var mDelete = CMD_DELETE.matcher(input);
            if (mDelete.matches()) {
                String s = mDelete.group(1).trim();
                delete(s);
                storage.save(tasks);
                continue;
            }

            if (NO.matcher(input).find()) {
                System.out.printf("Alright, it was nice talking to you %s! See you next time :D%n", name);
                break;
            } else if (YES.matcher(input).find()) {
                System.out.println("What else can I help you with?");
            } else {
                System.out.println("Noted. Type 'show' to see the todo list; 'help' for commands.");
            }

        }

    }

    private void show( Integer taskNumber) {
        if (tasks.isEmpty()) {
            System.out.println("Memory is empty.");
            return;
        }
        if (taskNumber == null) {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.printf("%3d) %s%n", i + 1, tasks.get(i));
            }
        } else {
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("Invalid task number.");
                return;
            }
            String task = tasks.get(taskNumber - 1).toString();
            System.out.printf("%3d) %s%n", taskNumber, task);
        }
    }

    private void complete(String target) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to complete.");
            return;
        }

        try {
            int idx = Integer.parseInt(target);
            if (idx < 1 || idx > tasks.size()) {
                System.out.println("Invalid task number: " + idx);
                return;
            }
            Task t = tasks.get(idx - 1);
            if (t.getStatus()) {
                System.out.println("Already completed: " + t.getName());
            } else {
                t.setStatus(true);
                System.out.println("Completed: " + t.getName());
            }
            return;
        } catch (NumberFormatException e) {
        }
        try {
            String wanted = target.trim();
            Task curr = tasks.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(wanted))
                    .findFirst()
                    .orElse(null);

            if (curr == null) {
                System.out.println("Task not found: " + target);
                return;
            }

            if (curr.getStatus()) {
                System.out.println("Already completed: " + curr.getName());
            } else {
                curr.setStatus(true);
                System.out.println("Completed: " + curr.getName());
            }
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void delete(String target) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to delete.");
            return;
        }

        try {
            int idx = Integer.parseInt(target);
            if (idx < 1 || idx > tasks.size()) {
                System.out.println("Invalid task number: " + idx);
                return;
            }
            Task t = tasks.remove(idx - 1);
            System.out.println("Deleted task: " + t.getName());
            return;
        } catch (NumberFormatException e) {
            String wanted = target.trim();
            Task curr = tasks.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(wanted))
                    .findFirst()
                    .orElse(null);

            if (curr == null) {
                System.out.println("Task not found: " + target);
                return;
            }
            tasks.remove(curr);
            System.out.println("Deleted task: " + curr.getName());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void help() {
        System.out.println("""
            Commands:
              show                   - display everything you've told me
              show N                 - display the last N entries
              exit / quit            - leave the chat
              add                    - add tasks to todolist
              add todo task          - t "name"
              add deadline task      - d "name"/"deadline"
              add event              - e "name"/"date"/"start time"/"end time
              complete task          - complete "name"/"number"
              delete task            - delete "name"/"number"
            """);
    }
}
