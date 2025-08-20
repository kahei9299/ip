import java.util.*;
import java.util.regex.*;

public class Yap {

    private static final Scanner sc = new Scanner(System.in);
    private static final Pattern NO_PATTERN = Pattern.compile("\\b(no|n|nah|nope)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern YES_PATTERN = Pattern.compile("\\b(yes|y|yeah|yup|sure)\\b", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {

        System.out.printf("Hello! I'm Yap your new best friend!\n");
        System.out.printf("May I know what's your name?\n");
        String name = sc.nextLine();
        System.out.printf("Hi %s, how may i help you today? :D\n", name);
        String requests = sc.nextLine();
        System.out.printf("So you want me to help with : %s\n", requests);
        System.out.printf("Is there anything else that i can help you with?\n");
        String answer = sc.nextLine();
        Matcher mNo = NO_PATTERN.matcher(answer);
        Matcher mYes = YES_PATTERN.matcher(answer);
        if (mNo.find()) {
            System.out.printf("Alright, it was nice talking to you %s! See you next time :D%n", name);
        } else if (mYes.find()) {
            System.out.println("What else can I help you with?");
        } else {
            System.out.println("Sorry, I didn’t understand. Please answer yes or no.");
        }
    }
}
