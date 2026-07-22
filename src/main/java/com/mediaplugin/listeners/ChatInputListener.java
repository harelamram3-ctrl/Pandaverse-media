package com.mediaplugin.listeners;

import com.mediaplugin.MediaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ChatInputListener implements Listener {

    private final MediaPlugin plugin;

    public ChatInputListener(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String awaiting = GUIListener.AWAITING_INPUT.get(uuid);
        if (awaiting == null) return;

        event.setCancelled(true);
        GUIListener.AWAITING_INPUT.remove(uuid);
        String text = event.getMessage();

        if (text.equalsIgnoreCase("cancel")) {
            event.getPlayer().sendMessage(org.bukkit.ChatColor.GRAY + "בוטל.");
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (awaiting.equals("giveaway")) {
                String message = plugin.msg("giveaway-broadcast")
                        .replace("%player%", event.getPlayer().getName())
                        .replace("%message%", text);
                plugin.getServer().broadcastMessage(message);
            } else if (awaiting.equals("broadcast")) {
                String message = plugin.msg("broadcast-format")
                        .replace("%player%", event.getPlayer().getName())
                        .replace("%message%", text);
                plugin.getServer().broadcastMessage(message);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // מסתיר הודעת הצטרפות משחקנים שנמצאים כרגע במצב הקלטה
        boolean anyoneRecording = !plugin.getDataManager().recordingSet().isEmpty();
        if (anyoneRecording) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        boolean anyoneRecording = !plugin.getDataManager().recordingSet().isEmpty();
        if (anyoneRecording) {
            event.setQuitMessage(null);
        }
    }
}
