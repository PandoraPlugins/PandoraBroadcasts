package me.nanigans.pandorabroadcasts.Broadcasts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class CustomBroadcasts extends TimerTask {
    private final String message;
    private final Map<String, Object> messageData;
    private Date startTime;
    private final boolean enabled;
    private final Timer timer;
    public CustomBroadcasts(String message, Map<String, Object> messageData){
        this.message = ChatColor.translateAlternateColorCodes('&', message);
        this.messageData = messageData;
        this.enabled = Boolean.parseBoolean(messageData.get("enabled").toString());
        this.timer = new Timer();
    }

    public Date getFirstMessageTime(){

        final Calendar calendar = Calendar.getInstance();
        Map<String, Object> time = ((Map<String, Object>) messageData.get("startTime"));
        if(time != null) {
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.get("minute").toString()));
            calendar.set(Calendar.SECOND, Integer.parseInt(time.get("second").toString()));
        }
        return calendar.getTime();

    }

    public boolean scheduleAtRepeatedTime(){
        if(enabled) {
            final long repeatTime = Long.parseLong(messageData.get("repeatTime").toString());
            if (startTime != null)
                timer.schedule(this, startTime, repeatTime);
            else timer.schedule(this, repeatTime);
        }
        return enabled;
    }

    @Override
    public void run() {
        if(enabled)
        Bukkit.getServer().broadcastMessage(message);
    }

    public Timer getTimer() {
        return timer;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
