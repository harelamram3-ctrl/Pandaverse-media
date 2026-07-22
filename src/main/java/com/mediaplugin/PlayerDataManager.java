package com.mediaplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager {

    private final MediaPlugin plugin;
    private final File file;
    private FileConfiguration data;

    // רשימת יוטיוברים קבועה (נשמרת לדיסק)
    private final Set<UUID> youtubers = new HashSet<>();
    // קישור אישי של כל יוטיובר (נשמר לדיסק)
    private final Map<UUID, String> links = new HashMap<>();

    // מצבים זמניים (לא נשמרים בין ריסטארטים) - מתאפסים כשהשרת עולה מחדש
    private final Set<UUID> liveNow = new HashSet<>();
    private final Set<UUID> vanished = new HashSet<>();
    private final Set<UUID> flying = new HashSet<>();
    private final Set<UUID> godMode = new HashSet<>();
    private final Set<UUID> recording = new HashSet<>();

    public PlayerDataManager(MediaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("לא הצלחתי ליצור players.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(file);

        youtubers.clear();
        links.clear();

        for (String key : data.getStringList("youtubers")) {
            try {
                youtubers.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (data.isConfigurationSection("links")) {
            for (String key : data.getConfigurationSection("links").getKeys(false)) {
                try {
                    links.put(UUID.fromString(key), data.getString("links." + key));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        if (data == null) data = new YamlConfiguration();

        data.set("youtubers", youtubers.stream().map(UUID::toString).toList());
        for (Map.Entry<UUID, String> entry : links.entrySet()) {
            data.set("links." + entry.getKey(), entry.getValue());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("לא הצלחתי לשמור players.yml: " + e.getMessage());
        }
    }

    public boolean isYoutuber(UUID uuid) {
        return youtubers.contains(uuid);
    }

    public boolean addYoutuber(UUID uuid) {
        boolean added = youtubers.add(uuid);
        save();
        return added;
    }

    public boolean removeYoutuber(UUID uuid) {
        boolean removed = youtubers.remove(uuid);
        links.remove(uuid);
        save();
        return removed;
    }

    public Set<UUID> getYoutubers() {
        return youtubers;
    }

    public void setLink(UUID uuid, String link) {
        links.put(uuid, link);
        save();
    }

    public String getLink(UUID uuid) {
        return links.getOrDefault(uuid, "");
    }

    // --- toggles ---
    public boolean toggle(Set<UUID> set, UUID uuid) {
        if (set.contains(uuid)) {
            set.remove(uuid);
            return false;
        } else {
            set.add(uuid);
            return true;
        }
    }

    public Set<UUID> liveSet() { return liveNow; }
    public Set<UUID> vanishSet() { return vanished; }
    public Set<UUID> flySet() { return flying; }
    public Set<UUID> godSet() { return godMode; }
    public Set<UUID> recordingSet() { return recording; }
}
