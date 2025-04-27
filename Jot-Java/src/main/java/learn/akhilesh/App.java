package learn.akhilesh;

import learn.akhilesh.dao.GrammarReport;
import learn.akhilesh.script.NotionCheck;

import java.util.List;
import java.util.Scanner;

public class App {
    public static final String WELCOME_MSG = """
            What task do you want to perform?
            1. Check your notion page?
            """;
    public static final String INPUT_PROMPT = """
            Enter your option:
            """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(WELCOME_MSG);
        System.out.println(INPUT_PROMPT);
        String command = scanner.nextLine();
        processInput(command, scanner);
    }

    private static void processInput(String command, Scanner scanner) {
        switch (command) {
            case "1" -> {
                NotionCheck notionCheck = new NotionCheck();
                System.out.println("""
                        Enter path of your .md converted notion page:
                        """);
                String filePath = scanner.nextLine();
                List<GrammarReport> report = notionCheck.generateGrammarReport(filePath);
                report.forEach(r -> System.out.println(r.toString()));
            }
            default -> System.out.println("Wrong command!");
        }
    }
}
