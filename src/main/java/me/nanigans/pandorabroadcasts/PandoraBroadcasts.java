package me.nanigans.pandorabroadcasts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.nanigans.pandorabroadcasts.Broadcasts.CustomBroadcasts;
import me.nanigans.pandorabroadcasts.Broadcasts.RestartCountdown;
import me.nanigans.pandorabroadcasts.Broadcasts.RestartTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class PandoraBroadcasts extends JavaPlugin {
    Map<String, CustomBroadcasts> broadcastsMap = new HashMap<>();
    private RestartTimer timer;
    GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new CustomizedObjectTypeAdapter());
    public HashMap map = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        final File configFile = new File(getDataFolder(), "broadcasts.json");

        if(!configFile.exists()) {

            saveResource(configFile.getName(), false);
            try {
                Gson gson = gsonBuilder.create();

                map = gson.fromJson(new FileReader(configFile), HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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
        assert restart != null;
        final RestartTimer restartTimer = new RestartTimer(restart);
        restartTimer.setStartTime(restartTimer.calculateStartTime());
        restartTimer.scheduleRepeat();
        this.timer = restartTimer;

        final Object data = restart.get("message");
        final Map<String, Object> countdownFrom = (Map<String, Object>) restart.get("countdownFrom");
        final long repeat = Long.parseLong(countdownFrom.get("interval").toString());
        final long from = Long.parseLong(countdownFrom.get("from").toString());
        final Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(restartTimer.getRestartTime());
        RestartCountdown countdown = new RestartCountdown(data.toString(), instance, restartTimer);
        Timer timer = new Timer();
        timer.schedule(countdown, new Date(restartTimer.getRestartTime()-from), repeat);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        broadcastsMap.forEach((i, j) -> {
            System.out.println("i = " + i);
            j.cancel();
        });
        Map<String, Object> restart = (Map<String, Object>) JsonUtil.getData("Restart_Broadcast");
        if(timer != null)
        this.timer.cancel();
        if(restart != null)
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', restart.get("onRestart").toString()));
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
  "onRestart": "The server is now restarting",
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