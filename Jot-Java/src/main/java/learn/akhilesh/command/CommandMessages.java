package learn.akhilesh.command;

public class CommandMessages {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    public static final String WELCOME_MSG = YELLOW + """
            What task do you want to perform?
            1. Check your notion page?
            0. Exit the application
            """ + RESET;
    public static final String ENTER_OPTION = BLUE + "Enter your option:" + RESET;
    public static final String INVALID_OPTION = RED + "Please enter a valid option!" + RESET;

    public static String createError(String msg) {
        return RED + msg + RESET;
    }

    public static String createSuccess(String msg) {
        return GREEN + msg + RESET;
    }

    public static String createMessage(String msg) {
        return YELLOW + msg + RESET;
    }

    public static String createInput(String msg) {
        return BLUE + msg + RESET;
    }
}
