import java.util.Scanner;

public class Yap {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Hello! I'm Yap your new best friend!");
        System.out.println("May I know what's your name?\n");
        String name = sc.nextLine();
        System.out.printf("Hi %s, how may i help you today? :D\n", name);
        System.out.printf("Alright it was nice talking to you %s! See you next time :D", name);
    }
}
