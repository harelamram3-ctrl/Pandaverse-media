package com.mediaplugin.listeners;

import com.mediaplugin.MediaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

    private final MediaPlugin plugin;

    public ChatInputListener(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public onChat(AsyncPlayerChatEvent event) {
        // לוגיקת צ'אט במידת הצורך
    }
}
