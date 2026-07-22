package com.mediaplugin;

import com.mediaplugin.commands.MediaCommand;
import com.mediaplugin.listeners.ChatInputListener;
import com.mediaplugin.listeners.GUIListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MediaPlugin extends JavaPlugin {

    private static MediaPlugin instance;
    private PlayerDataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        dataManager = new PlayerDataManager(this);
        dataManager.load();

        MediaCommand mediaCommand = new MediaCommand(this);
        getCommand("media").setExecutor(mediaCommand);
        getCommand("media").setTabCompleter(mediaCommand);

        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this), this);

        getLogger().info("MediaPlugin הופעל בהצלחה!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
        getLogger().info("MediaPlugin כובה.");
    }

    public static MediaPlugin get() {
        return instance;
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    /**
     * שחקן נחשב בעל גישה לפיצ'ר אם הוא ברשימת היוטיוברים הפנימית של הפלאגין
     * (נוסף דרך /media add), או אם יש לו את ה-permission node המתאים
     * (למשל דרך LuckPerms) - כך שהפלאגין עובד גם בלי פלאגין הרשאות חיצוני.
     */
    public boolean hasAccess(Player player, String node) {
        if (player.isOp()) return true;
        if (dataManager.isYoutuber(player.getUniqueId())) return true;
        return player.hasPermission(node);
    }

    public String msg(String key) {
        String raw = getConfig().getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String guiTitle() {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("gui.title", "&b&lMedia Panel"));
    }
}
