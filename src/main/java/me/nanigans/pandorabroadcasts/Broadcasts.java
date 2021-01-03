package me.nanigans.pandorabroadcasts;

import org.bukkit.Bukkit;

import java.util.Timer;
import java.util.TimerTask;

public class Broadcasts extends TimerTask {
    private final String message;
    public Broadcasts(String message){
        this.message = message;
        
    }

    @Override
    public void run() {
        Bukkit.getServer().broadcastMessage(message);
    }
}
