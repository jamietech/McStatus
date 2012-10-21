package tk.nekotech.mcstatus;

import java.util.TimerTask;
import org.jibble.pircbot.Colors;

public class StatusCheck extends TimerTask {
    private final McStatus mcstatus;
    private final Status[] statuses;

    public StatusCheck(final McStatus mcstatus) {
        this.mcstatus = mcstatus;
        this.statuses = new Status[3];
    }

    private String getColored(final Status status) {
        if (status == null) {
            return Colors.YELLOW + "Unknown" + Colors.NORMAL;
        }
        switch (status) {
            case UP:
                return Colors.GREEN + "Up" + Colors.NORMAL;
            case DOWN:
                return Colors.RED + "Down" + Colors.NORMAL;
            case UNKNOWN:
                return Colors.YELLOW + "Unknown" + Colors.NORMAL;
        }
        return null;
    }

    @Override
    public void run() {
        final Status login = this.statuses[0];
        final Status session = this.statuses[1];
        final Status skins = this.statuses[2];
        this.statuses[0] = this.mcstatus.getStatus("https://login.minecraft.net/?user=jamietech&password=uptimechecker&version=12");
        this.statuses[1] = this.mcstatus.getStatus("http://session.minecraft.net/game/joinserver.jsp?user=jamietech&sessionId=notASessionId&serverId=uptimeChecker");
        this.statuses[2] = this.mcstatus.getStatus("http://s3.amazonaws.com/MinecraftSkins/BurningFurnace.png");
        final StringBuilder sb = new StringBuilder();
        sb.append("Minecraft Status Checker | www.minecraft.net | ");
        sb.append(Colors.BOLD + "Login " + Colors.NORMAL + this.getColored(this.statuses[0]));
        sb.append(Colors.BOLD + " Session " + Colors.NORMAL + this.getColored(this.statuses[1]));
        sb.append(Colors.BOLD + " Skins " + Colors.NORMAL + this.getColored(this.statuses[2]));
        final String topic = sb.toString();
        sb.delete(0, sb.length());
        if (!topic.equals(this.mcstatus.topic)) {
            this.mcstatus.setTopic("#McStatus", topic);
            if (login != this.statuses[0]) {
                sb.append("Login: " + this.getColored(login) + " -> " + this.getColored(this.statuses[0]) + ", ");
            }
            if (session != this.statuses[1]) {
                sb.append("Session: " + this.getColored(session) + " -> " + this.getColored(this.statuses[1]) + ", ");
            }
            if (skins != this.statuses[2]) {
                sb.append("Session" + this.getColored(skins) + " -> " + this.getColored(this.statuses[2]) + ", ");
            }
            if (sb.length() != 0) {
                sb.delete(sb.length() - 2, sb.length());
                this.mcstatus.sendMessage("#McStatus", Colors.BOLD + "The following changes in services were noted this check - " + Colors.NORMAL + sb.toString());
            }
        }
    }
}
