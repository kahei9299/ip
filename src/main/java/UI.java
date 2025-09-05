import java.util.Scanner;

public class UI {
    private final Scanner in = new Scanner(System.in);

    public void showWelcome() {
        System.out.println("Hello! I'm Yap\nWhat can I do for you?");
    }

    public String readCommand() {
        return in.nextLine();
    }

    public void showLine() {
        System.out.println("--------------------------------------------------");
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showError(String error) {
        System.out.println("☹ OOPS! " + error);
    }

    public void showLoadingError() {
        showError("I couldn't load your saved tasks. Starting fresh!");
    }

    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }
}
