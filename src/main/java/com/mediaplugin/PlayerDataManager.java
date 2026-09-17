package com.mediaplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {

    private final MediaPlugin plugin;
    private File file;
    private FileConfiguration dataConfig;

    public PlayerDataManager(MediaPlugin plugin) {
        this.plugin = plugin;
        createFile();
    }

    private void createFile() {
        file = new File(plugin.getDataFolder(), "mediaplayers.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getData() {
        return dataConfig;
    }

    public void saveData() {
        try {
            dataConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerLink(UUID uuid, String url) {
        dataConfig.set("media." + uuid.toString() + ".link", url);
        saveData();
    }

    public String getPlayerLink(UUID uuid) {
        return dataConfig.getString("media." + uuid.toString() + ".link", "אין קישור מוגדר");
    }

    public boolean isMediaMember(UUID uuid) {
        return dataConfig.contains("media." + uuid.toString());
    }

    public void addMediaMember(UUID uuid, String name) {
        dataConfig.set("media." + uuid.toString() + ".name", name);
        dataConfig.set("media." + uuid.toString() + ".link", "טרם הוגדר");
        saveData();
    }

    public void removeMediaMember(UUID uuid) {
        dataConfig.set("media." + uuid.toString(), null);
        saveData();
    }
}
