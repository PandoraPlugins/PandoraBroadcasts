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
            j.cancel();
        });
        Map<String, Object> restart = (Map<String, Object>) JsonUtil.getData("Restart_Broadcast");
        if(timer != null)
        this.timer.cancel();
        if(restart != null)
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', restart.get("onRestart").toString()));
    }
}
