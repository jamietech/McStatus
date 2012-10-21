package tk.nekotech.mcstatus;

import java.util.Scanner;

public class Listener extends Thread {
    McStatus mcstatus;
    protected boolean pleaseStop = false;

    public Listener(final McStatus mcstatus) {
        this.mcstatus = mcstatus;
    }

    @Override
    public void run() {
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            if (this.pleaseStop) {
                break;
            }
            final String line = scanner.nextLine();
            this.mcstatus.sendRawLine(line);
            if (line.startsWith("QUIT")) {
                this.mcstatus.forceQuit = true;
            }
        }
        System.out.println("Stopping Listener thread!");
    }
}
