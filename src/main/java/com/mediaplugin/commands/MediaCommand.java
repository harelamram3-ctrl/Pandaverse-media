package com.mediaplugin.commands;

import com.mediaplugin.MediaPlugin;
import com.mediaplugin.gui.MediaGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MediaCommand implements CommandExecutor {

    private final MediaPlugin plugin;

    public MediaCommand(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("הפקודה הזו ניתנת לביצוע רק במשחק על ידי שחקן.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("media.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&b&lPandaverse&f&lMedia&8] &cאין לך הרשאה להשתמש בפקודה זו!"));
            return true;
        }

        if (args.length == 0) {
            MediaGUI.openGUI(player);
            return true;
        }

        // ניהול מנהלים (Admin Commands)
        if (args[0].equalsIgnoreCase("add")) {
            if (!player.hasPermission("media.admin")) {
                player.sendMessage(ChatColor.RED + "אין לך הרשאה לנהל חברי מדיה.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.YELLOW + "שימוש: /media add [שחקן]");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            plugin.getDataManager().addMediaMember(target.getUniqueId(), target.getName() != null ? target.getName() : "Unknown");
            player.sendMessage(ChatColor.GREEN + "השחקן " + target.getName() + " נוסף בהצלחה לרשימת המדיה של Pandaverse!");
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (!player.hasPermission("media.admin")) {
                player.sendMessage(ChatColor.RED + "אין לך הרשאה לנהל חברי מדיה.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.YELLOW + "שימוש: /media remove [שחקן]");
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            plugin.getDataManager().removeMediaMember(target.getUniqueId());
            player.sendMessage(ChatColor.RED + "השחקן " + target.getName() + " הוסר מרשימת המדיה.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setlink")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.YELLOW + "שימוש: /media setlink [קישור]");
                return true;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String link = sb.toString().trim();
            plugin.getDataManager().setPlayerLink(player.getUniqueId(), link);
            player.sendMessage(ChatColor.AQUA + "הקישור שלך לערוץ/לייב עודכן בהצלחה ל: " + ChatColor.WHITE + link);
            return true;
        }

        MediaGUI.openGUI(player);
        return true;
    }
}
