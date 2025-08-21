import java.util.*;
import java.util.regex.*;

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

    public static void main(String[] args) {

        System.out.printf("Hello! I'm Yap your new best friend!\n");
        String name = ask("May I know what's your name?");
        System.out.printf("Hi %s, how may i help you today? :D\n", name);
        System.out.println("for a list of available commands, type 'help' ");

        List<Task> memory = new ArrayList<>();
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

                        if (firstletter == 't') {
                            if (cleanedInput.charAt(1) != ' ') {
                                throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
                            }
                            String taskDetails = cleanedInput.substring(1).trim();
                            memory.add(new ToDos(taskDetails));
                            System.out.println("Added ToDo task: " + taskDetails);
                        } else if (firstletter == 'd') {
                            if (cleanedInput.charAt(1) != ' ') {
                                throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
                            }
                            String taskDetails = cleanedInput.substring(1).trim();
                            String[] parts = taskDetails.split("/");
                            memory.add(new Deadlines(parts[0], parts[1]));
                            System.out.println(("Added Deadline task: " + parts[0]));
                        } else if (firstletter == 'e') {
                            if (cleanedInput.charAt(1) != ' ') {
                                throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
                            }
                            String taskDetails = cleanedInput.substring(1).trim();
                            String[] parts = taskDetails.split("/");
                            memory.add(new Events(parts[0], parts[1], parts[2], parts[3]));
                            System.out.println("Added Event: " + parts[0]);
                        } else {
                            throw new InvalidTaskTypeException("Unknown task type please re-enter based on the specified format");
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
                show(memory, n);
                continue;
            }

            if (CMD_HELP.matcher(input).matches()) {
                help();
                continue;
            }

            var mComplete = CMD_COMPLETE.matcher(input);
            if(mComplete.matches()) {
                String s = mComplete.group(1).trim();
                complete(memory, s);
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

    private static String ask(String prompt) {
        while (true) {
            String s = read(prompt + " ");
            if (!s.isBlank()) return s.trim();
            System.out.println("Please enter something.");
        }
    }

    private static String read(String prompt) {
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

    private static void show(List<Task> memory, Integer taskNumber) {
        if (memory.isEmpty()) {
            System.out.println("Memory is empty.");
            return;
        }
        if (taskNumber == null) {
            for (int i = 0; i < memory.size(); i++) {
                System.out.printf("%3d) %s%n", i + 1, memory.get(i));
            }
        } else {
            if (taskNumber < 1 || taskNumber > memory.size()) {
                System.out.println("Invalid task number.");
                return;
            }
            String task = memory.get(taskNumber - 1).toString();
            System.out.printf("%3d) %s%n", taskNumber, task);
        }
    }

    private static void complete(List<Task> memory, String target) {
        if (memory.isEmpty()) {
            System.out.println("No tasks to complete.");
            return;
        }

        try {
            int idx = Integer.parseInt(target);
            if (idx < 1 || idx > memory.size()) {
                System.out.println("Invalid task number: " + idx);
                return;
            }
            Task t = memory.get(idx - 1);
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
            Task curr = memory.stream()
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

    private static void help() {
        System.out.println("""
            Commands:
              show                   - display everything you've told me
              show N                 - display the last N entries
              exit / quit            - leave the chat
              add                    - add tasks to todolist
              add todo task          - t "name"
              add deadline task      - d "name"/"deadline"
              add event              - e "name"/"date"/"start time"/"end time
            """);
    }
}
