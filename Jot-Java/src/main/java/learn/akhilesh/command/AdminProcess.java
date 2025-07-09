package learn.akhilesh.command;

import learn.akhilesh.dao.GrammarReport;
import learn.akhilesh.script.NotionCheck;

import java.util.List;
import java.util.Scanner;

import static learn.akhilesh.command.CommandMessages.*;

public class AdminProcess {

    public Scanner scanner;

    public AdminProcess() {
        this.scanner = new Scanner(System.in);
    }

    public String appStart() {
        System.out.println(WELCOME_MSG);
        System.out.println(ENTER_OPTION);
        return scanner.nextLine();
    }

    public void processInput(String command) {
        try {
            switch (command) {
                case "1" -> {
                    NotionCheck notionCheck = new NotionCheck();
                    System.out.println("Enter path of your .md converted notion page: ");
                    String filePath = scanner.nextLine();
                    List<GrammarReport> report = notionCheck.generateGrammarReport(filePath);
                    report.forEach(r -> System.out.println(r.toString()));
                }
                case "0" -> {
                    System.out.println("Exiting the application. Goodbye!");
                    scanner.close();
                    System.exit(0);
                }
                default -> {
                    System.out.println(INVALID_OPTION);
                    command = appStart();
                    processInput(command);
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while processing your request: " + e.getMessage());
            System.out.println(INVALID_OPTION);
            command = appStart();
            processInput(command);
        } finally {
            command = appStart();
            processInput(command);
        }
    }
}
