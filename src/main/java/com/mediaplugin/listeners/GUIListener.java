package com.mediaplugin.listeners;

import com.mediaplugin.MediaPlugin;
import com.mediaplugin.gui.MediaGUI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final MediaPlugin plugin;
    // שחקנים שממתינים להקליד הודעת שידור/הגרלה בצ'אט
    public static final Map<UUID, String> AWAITING_INPUT = new HashMap<>();

    public GUIListener(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getView().getTitle()).equals(ChatColor.stripColor(plugin.guiTitle()))) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, MediaGUI.NBT_KEY);
        String actionName = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (actionName == null) return;

        MediaGUI.MediaAction action;
        try {
            action = MediaGUI.MediaAction.valueOf(actionName);
        } catch (IllegalArgumentException e) {
            return;
        }

        UUID uuid = player.getUniqueId();

        switch (action) {
            case CLOSE -> player.closeInventory();

            case NONE, INFO_ONLY -> {
                if (action == MediaGUI.MediaAction.NONE) {
                    player.sendMessage(plugin.msg("no-access-item"));
                }
            }

            case TOGGLE_LIVE -> {
                boolean nowLive = plugin.getDataManager().toggle(plugin.getDataManager().liveSet(), uuid);
                if (nowLive) {
                    String link = plugin.getDataManager().getLink(uuid);
                    String message = plugin.msg("live-on")
                            .replace("%player%", player.getName())
                            .replace("%link%", link == null || link.isEmpty() ? "" : link);
                    plugin.getServer().broadcastMessage(message);
                } else {
                    plugin.getServer().broadcastMessage(plugin.msg("live-off").replace("%player%", player.getName()));
                }
                player.closeInventory();
            }

            case GIVEAWAY -> {
                AWAITING_INPUT.put(uuid, "giveaway");
                player.closeInventory();
                player.sendMessage(plugin.msg("giveaway-prompt"));
            }

            case BROADCAST -> {
                AWAITING_INPUT.put(uuid, "broadcast");
                player.closeInventory();
                player.sendMessage(plugin.msg("broadcast-prompt"));
            }

            case TOGGLE_VANISH -> {
                boolean now = plugin.getDataManager().toggle(plugin.getDataManager().vanishSet(), uuid);
                for (Player other : plugin.getServer().getOnlinePlayers()) {
                    if (other.equals(player)) continue;
                    if (now) {
                        other.hidePlayer(plugin, player);
                    } else {
                        other.showPlayer(plugin, player);
                    }
                }
                player.sendMessage(now ? plugin.msg("vanish-on") : plugin.msg("vanish-off"));
                refresh(player);
            }

            case TOGGLE_FLY -> {
                boolean now = plugin.getDataManager().toggle(plugin.getDataManager().flySet(), uuid);
                player.setAllowFlight(now);
                player.setFlying(now);
                player.sendMessage(now ? plugin.msg("fly-on") : plugin.msg("fly-off"));
                refresh(player);
            }

            case HEAL -> {
                player.setHealth(player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setFoodLevel(20);
                player.setSaturation(20f);
                player.sendMessage(plugin.msg("healed"));
            }

            case WEATHER -> {
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
                player.sendMessage(plugin.msg("weather-cleared"));
            }

            case TIME -> {
                player.getWorld().setTime(1000);
                player.sendMessage(plugin.msg("time-set"));
            }

            case TOGGLE_GOD -> {
                boolean now = plugin.getDataManager().toggle(plugin.getDataManager().godSet(), uuid);
                player.setInvulnerable(now);
                player.sendMessage(now ? plugin.msg("god-on") : plugin.msg("god-off"));
                refresh(player);
            }

            case TOGGLE_RECORDING -> {
                boolean now = plugin.getDataManager().toggle(plugin.getDataManager().recordingSet(), uuid);
                if (now) {
                    for (int i = 0; i < 100; i++) player.sendMessage("");
                }
                player.sendMessage(now ? plugin.msg("recording-on") : plugin.msg("recording-off"));
                refresh(player);
            }
        }
    }

    private void refresh(Player player) {
        if (player.getOpenInventory() != null) {
            player.openInventory(new MediaGUI(plugin).build(player));
        }
    }
}
