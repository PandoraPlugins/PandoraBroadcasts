package me.nanigans.pandorabroadcasts.Broadcasts;

import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class RestartCountdown extends TimerTask {

    private final String message;
    private final Calendar endTime;
    private final RestartTimer t;
    public RestartCountdown(String message, Calendar endTime, RestartTimer t){
        this.message = message;
        this.endTime = endTime;
        this.t = t;
    }

    @Override
    public void run() {
        t.cancel();
        final String replacement = DateParser.formatDateDiff(Calendar.getInstance(), endTime);
        if(!replacement.contains("now"))
        Bukkit.getServer().broadcastMessage(this.message.replaceAll("\\{time}",
                replacement)
        );
        if(new Date().getTime() >= endTime.getTimeInMillis())
            this.cancel();
    }
}
