package me.nanigans.pandorabroadcasts.Broadcasts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class RestartTimer extends TimerTask {

    private final String message;
    private final Map<String, Object> restartDate;
    private final Timer timer = new Timer();
    private final Timer countDownTimer = new Timer();
    private Date startTime;
    private final long restartTime;

    public RestartTimer(String message, Map<String, Object> restartDate) {
        this.message = ChatColor.translateAlternateColorCodes('&', message);
        this.restartDate = restartDate;
        this.restartTime = new Date().getTime()+Long.parseLong(restartDate.get("restartTime").toString());
        final Map<String, Object> countdownFrom = (Map<String, Object>) restartDate.get("countdownFrom");

        if(countdownFrom != null)
            countDownTimer.schedule(this, restartTime-Long.parseLong(countdownFrom.get("from").toString()));
    }

    public Date calculateStartTime(){
        final Calendar calendar = Calendar.getInstance();
        long restartTime = calendar.getTimeInMillis() + Long.parseLong(restartDate.get("restartTime").toString());
        restartTime -= Long.parseLong(restartDate.get("startWarnTime").toString());
        calendar.setTimeInMillis(restartTime);
        return calendar.getTime();
    }

    public void scheduleRepeat(){
        timer.schedule(this, this.startTime, Long.parseLong(restartDate.get("repeatWarn").toString()));
    }

    @Override
    public void run() {
        final Calendar endTime = Calendar.getInstance();
        endTime.setTime(new Date(restartTime));

        Bukkit.getServer().broadcastMessage(this.message.replaceAll("\\{time}",
                DateParser.formatDateDiff(Calendar.getInstance(), endTime))
                );
        if(new Date().getTime() > restartTime)
            this.cancel();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
