package tk.nekotech.mcstatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

public class McStatus extends PircBot {
    public static void main(final String[] args) throws Exception {
        new McStatus();
    }

    private final Timer timer;
    protected String topic;
    protected boolean forceQuit = false;
    private final Listener listener;
    private String auth;

    public McStatus() throws NickAlreadyInUseException, IOException, IrcException {
        System.out.println("Starting...");
        File file = new File("config.cfg");
        if (!file.exists()) {
            try {
                file.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write("# McStatus configuration");
                out.newLine();
                out.write("authline: mietech pass");
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String nextLine;
            while ((nextLine = in.readLine()) != null) {
                if (nextLine.startsWith("#")) {
                    continue;
                }
                if (nextLine.startsWith("authline: ")) {
                    auth = nextLine.replace("authline: ", "");
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.setVerbose(true);
        this.setAutoNickChange(true);
        this.setFinger("McStatus bot. Help in #jamietechop");
        this.setLogin("j");
        this.setName("McStatus");
        this.setVersion("McStatus bot v0.1. Help in #jamietechop");
        this.connect("Prothid.CA.US.GameSurge.net", 6667);
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new StatusCheck(this), 5000, 60000);
        this.listener = new Listener(this);
        this.listener.start();
    }

    protected Status getStatus(final String urlToCheck) {
        try {
            final URL url = new URL(urlToCheck);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            final int code = connection.getResponseCode();
            connection.disconnect();
            return Status.fromStatusCode(code);
        } catch (final Exception e) {
            return Status.UNKNOWN;
        }
    }

    @Override
    public void onConnect() {
        this.sendMessage("AuthServ@Services.GameSurge.net", "auth " + auth);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDisconnect() {
        if (!this.forceQuit) {
            try {
                this.reconnect();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        } else {
            System.out.println("Forced quit detected! Shutting down...");
            System.out.println("Good-bye cruel world!");
            this.dispose();
            this.listener.stop();
            System.exit(1);
        }
    }

    @Override
    public void onNotice(final String sourceNick, final String sourceLogin, final String sourceHostname, final String target, final String notice) {
        if (sourceNick.equals("AuthServ") && notice.startsWith("I recogni")) {
            this.joinChannel("#McStatus");
        }
    }

    @Override
    public void onTopic(final String channel, final String topic, final String setBy, final long date, final boolean changed) {
        if (channel.equals("#McStatus")) {
            this.topic = topic;
        }
    }
}
