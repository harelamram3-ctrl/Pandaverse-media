package com.mediaplugin;

import com.mediaplugin.commands.MediaCommand;
import com.mediaplugin.listeners.ChatInputListener;
import com.mediaplugin.listeners.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class MediaPlugin extends JavaPlugin {

    private static MediaPlugin instance;
    private PlayerDataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // אתכול מנהל הנתונים
        dataManager = new PlayerDataManager(this);

        // רישום פקודות
        if (getCommand("media") != null) {
            getCommand("media").setExecutor(new MediaCommand(this));
        }

        // רישום אירועים (Listeners)
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', 
            "&8[&b&lPandaverse&f&lMedia&8] &aהפלאגין הופעל בהצלחה בשרת PANDAVERSE!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', 
            "&8[&b&lPandaverse&f&lMedia&8] &cהפלאגין כובה."));
    }

    public static MediaPlugin getInstance() {
        return instance;
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }
}
