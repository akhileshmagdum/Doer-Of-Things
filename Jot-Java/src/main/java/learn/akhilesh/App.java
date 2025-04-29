package learn.akhilesh;

import learn.akhilesh.command.AdminProcess;

public class App {

    public static void main(String[] args) {
        AdminProcess admin = new AdminProcess();
        admin.processInput(admin.appStart());
    }
}
