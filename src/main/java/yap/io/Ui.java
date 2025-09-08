package yap.io;

import java.util.Scanner;

public class Ui {
  private final Scanner in = new Scanner(System.in);

  public void showWelcome() {
    System.out.println("Hello! I'm Yap your new best friend!");
  }

  public String askName() {
    System.out.println("May I know what's your name?");
    String name = in.nextLine().trim();
    while (name.isEmpty()) {
      System.out.println("Sorry, I didn't catch that. What's your name?");
      name = in.nextLine().trim();
    }
    System.out.printf("Nice to meet you, %s!%n", name);
    System.out.printf("How can i help you today?");
    return name;
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

  public void showGoodbye(String name) {
    System.out.printf("Goodbye, %s!%n", name);
  }
}
