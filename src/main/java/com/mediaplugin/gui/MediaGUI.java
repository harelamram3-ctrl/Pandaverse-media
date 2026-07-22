package com.mediaplugin.gui;

import com.mediaplugin.MediaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MediaGUI {

    // מפתחות הסלוטים - נשמרים כדי שה-Listener ידע על מה לחצו
    public static final String NBT_KEY = "media_action";

    private final MediaPlugin plugin;

    public MediaGUI(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, plugin.guiTitle());
        UUID uuid = player.getUniqueId();

        inv.setItem(10, item(Material.REDSTONE_BLOCK,
                plugin.getDataManager().liveSet().contains(uuid) ? "&c&l⚫ אני בלייב! (פעיל)" : "&c&l⚫ אני בלייב!",
                List.of("&7לחץ כדי להכריז לכל השרת", "&7שאתה עולה/יורד משידור חי"),
                MediaAction.TOGGLE_LIVE, "media.live", player));

        inv.setItem(11, item(Material.NETHER_STAR,
                "&e&l🎉 הודעת הגרלה",
                List.of("&7לחץ כדי לכתוב הודעת הגרלה", "&7שתשודר לכל השרת"),
                MediaAction.GIVEAWAY, "media.giveaway", player));

        inv.setItem(12, item(Material.ENDER_EYE,
                plugin.getDataManager().vanishSet().contains(uuid) ? "&8&l👁 וניש (פעיל)" : "&8&l👁 וניש",
                List.of("&7הפוך לבלתי נראה לשחקנים אחרים"),
                MediaAction.TOGGLE_VANISH, "media.vanish", player));

        inv.setItem(13, item(Material.FEATHER,
                plugin.getDataManager().flySet().contains(uuid) ? "&b&l🕊 טיסה (פעיל)" : "&b&l🕊 טיסה",
                List.of("&7הפעל/כבה מצב טיסה"),
                MediaAction.TOGGLE_FLY, "media.fly", player));

        inv.setItem(14, item(Material.GOLDEN_APPLE,
                "&a&l❤ מילוי חיים ורעב",
                List.of("&7מלא לעצמך חיים ורעב במלואם", "&7- מושלם לפני שידור"),
                MediaAction.HEAL, "media.heal", player));

        inv.setItem(15, item(Material.SUNFLOWER,
                "&e&l☀ נקה מזג אוויר",
                List.of("&7מנקה גשם/סערה - תאורה טובה יותר לצילום"),
                MediaAction.WEATHER, "media.weather", player));

        inv.setItem(16, item(Material.CLOCK,
                "&e&l🕐 קבע זמן ליום",
                List.of("&7משנה את הזמן ליום מיידית"),
                MediaAction.TIME, "media.time", player));

        inv.setItem(19, item(Material.TOTEM_OF_UNDYING,
                plugin.getDataManager().godSet().contains(uuid) ? "&d&l✦ אלמוות (פעיל)" : "&d&l✦ אלמוות",
                List.of("&7מצב חסין נזק - לא תמות בשידור"),
                MediaAction.TOGGLE_GOD, "media.god", player));

        inv.setItem(20, item(Material.PAPER,
                "&b&l📢 שדר הודעה לשרת",
                List.of("&7כתוב הודעה מעוצבת", "&7שתישלח לכל השחקנים בשרת"),
                MediaAction.BROADCAST, "media.broadcast", player));

        inv.setItem(21, item(Material.RED_DYE,
                plugin.getDataManager().recordingSet().contains(uuid) ? "&c&l🎥 מצב הקלטה (פעיל)" : "&c&l🎥 מצב הקלטה",
                List.of("&7מנקה את הצ'אט שלך", "&7ומסתיר הודעות כניסה/יציאה", "&7- נקי יותר להקלטה"),
                MediaAction.TOGGLE_RECORDING, "media.recording", player));

        inv.setItem(22, item(Material.NAME_TAG,
                "&f&l🔗 עדכן קישור שידור",
                List.of("&7הקלד: /media setlink <קישור>", "&7הקישור יופיע בהודעת ה'לייב' שלך"),
                MediaAction.INFO_ONLY, "media.live", player));

        inv.setItem(26, item(Material.BARRIER, "&c&lסגור", List.of(), MediaAction.CLOSE, null, player));

        return inv;
    }

    private ItemStack item(Material mat, String name, List<String> loreLines, MediaAction action, String permNode, Player player) {
        boolean hasAccess = permNode == null || plugin.hasAccess(player, permNode);

        ItemStack stack;
        try {
            stack = new ItemStack(hasAccess ? mat : Material.GRAY_DYE);
        } catch (Exception e) {
            stack = new ItemStack(hasAccess ? Material.PAPER : Material.GRAY_DYE);
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', hasAccess ? name : "&7" + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name))));

        List<String> lore = new ArrayList<>();
        if (hasAccess) {
            for (String l : loreLines) lore.add(ChatColor.translateAlternateColorCodes('&', l));
        } else {
            lore.add(ChatColor.translateAlternateColorCodes('&', plugin.msg("no-access-item")));
        }
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(plugin, NBT_KEY),
                org.bukkit.persistence.PersistentDataType.STRING,
                hasAccess ? action.name() : MediaAction.NONE.name());

        stack.setItemMeta(meta);
        return stack;
    }

    public enum MediaAction {
        TOGGLE_LIVE, GIVEAWAY, TOGGLE_VANISH, TOGGLE_FLY, HEAL, WEATHER, TIME,
        TOGGLE_GOD, BROADCAST, TOGGLE_RECORDING, INFO_ONLY, CLOSE, NONE
    }
}
