package me.nanigans.pandorabroadcasts;

import me.nanigans.pandorabroadcasts.Broadcasts.CustomBroadcasts;
import me.nanigans.pandorabroadcasts.Broadcasts.RestartTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class PandoraBroadcasts extends JavaPlugin {
    Map<String, CustomBroadcasts> broadcastsMap = new HashMap<>();
    private RestartTimer timer;

    @Override
    public void onEnable() {
        // Plugin startup logic

        Map<String, Object> braodcasts = (Map<String, Object>) JsonUtil.getData("Custom_Broadcasts");
        if(braodcasts != null && braodcasts.size() > 0) {
            braodcasts.forEach((i, j) -> {
                final CustomBroadcasts customBroadcasts = new CustomBroadcasts(((Map<String, Object>) j));
                customBroadcasts.setStartTime(customBroadcasts.getFirstMessageTime());
                customBroadcasts.scheduleAtRepeatedTime();
                broadcastsMap.put(i, customBroadcasts);
            });
        }

        final Map<String, Object> restart = (Map<String, Object>) JsonUtil.getData("Restart_Broadcast");
        final RestartTimer restartTimer = new RestartTimer(restart);
        restartTimer.setStartTime(restartTimer.getStartTime());
        restartTimer.scheduleRepeat();
        this.timer = restartTimer;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        broadcastsMap.forEach((i, j) -> j.cancel());
        Map<String, Object> restart = (Map<String, Object>) JsonUtil.getData("Restart_Broadcast");
        this.timer.cancel();
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', restart.get("message").toString()));
    }
}


/*
{
  "_comment": "The start time is when the broadcasts starts in the hour the server starts up. Set to <null> to send as soon as the server starts",
  "_comment1": "The repeatTime is in milliseconds for when the message should send after the start time",

  "Custom_Broadcasts": {
    "discord_broadcast": {
      "enabled": true,
      "message": "Check out our discord here! discord.com/PandoraPVP",
      "startTime": {
        "minute": 30,
        "second": 20
      },
      "repeatTime": 1800000
    }
  },
  "Restart_Broadcast": {
    "message": "The server will restart in {time}",
    "_comment": "restartTime is the time the server will be up before the next scheduled restart",
    "restartTime": 10800000,
    "_comment1": "startWarnTime is when it should start broadcasting before restart",
    "startWarnTime": 1800000,
    "_comment2": "repeatWarn is the interval in which it should warn the restart until it happens",
    "repeatWarn": 300000,
    "countdownFrom": {
      "_comment": "this will countdown from 5 seconds every 1 second",
      "from": 5000,
      "interval": 1000
    }
  }

}
 */