package com.mediaplugin.commands;

import com.mediaplugin.MediaPlugin;
import com.mediaplugin.gui.MediaGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MediaCommand implements CommandExecutor, TabCompleter {

    private final MediaPlugin plugin;

    public MediaCommand(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("פקודה זו זמינה רק לשחקנים.");
                return true;
            }
            if (!plugin.hasAccess(player, "media.use")) {
                player.sendMessage(plugin.msg("no-permission"));
                return true;
            }
            player.openInventory(new MediaGUI(plugin).build(player));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "add" -> {
                if (!sender.hasPermission("media.admin") && !sender.isOp()) {
                    sender.sendMessage(plugin.msg("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.msg("admin-usage"));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                    sender.sendMessage(plugin.msg("admin-not-found"));
                    return true;
                }
                plugin.getDataManager().addYoutuber(target.getUniqueId());
                sender.sendMessage(plugin.msg("admin-added").replace("%player%", args[1]));
                return true;
            }
            case "remove" -> {
                if (!sender.hasPermission("media.admin") && !sender.isOp()) {
                    sender.sendMessage(plugin.msg("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.msg("admin-usage"));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                plugin.getDataManager().removeYoutuber(target.getUniqueId());
                sender.sendMessage(plugin.msg("admin-removed").replace("%player%", args[1]));
                return true;
            }
            case "list" -> {
                if (!sender.hasPermission("media.admin") && !sender.isOp()) {
                    sender.sendMessage(plugin.msg("no-permission"));
                    return true;
                }
                sender.sendMessage(plugin.msg("admin-list-header"));
                for (UUID uuid : plugin.getDataManager().getYoutubers()) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    sender.sendMessage(" - " + (p.getName() == null ? uuid.toString() : p.getName()));
                }
                return true;
            }
            case "setlink" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("פקודה זו זמינה רק לשחקנים.");
                    return true;
                }
                if (!plugin.hasAccess(player, "media.live")) {
                    player.sendMessage(plugin.msg("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(plugin.msg("setlink-usage"));
                    return true;
                }
                plugin.getDataManager().setLink(player.getUniqueId(), args[1]);
                player.sendMessage(plugin.msg("setlink-success").replace("%link%", args[1]));
                return true;
            }
            default -> {
                sender.sendMessage(plugin.msg("admin-usage"));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "list", "setlink");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return new ArrayList<>();
    }
}
