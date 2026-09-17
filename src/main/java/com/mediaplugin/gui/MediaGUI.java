package com.mediaplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MediaGUI {

    public static void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Pandaverse Media Hub");

        // 1. כפתור הכרזה על לייב (רצועה 10)
        if (player.hasPermission("media.live")) {
            gui.setItem(10, createGuiItem(Material.RED_WOOL, "&c&lהכרזת לייב", "&7לחץ כדי להכריז על לייב פעיל בשרת!"));
        }

        // 2. כפתור הגרלה (רצועה 11)
        if (player.hasPermission("media.giveaway")) {
            gui.setItem(11, createGuiItem(Material.GOLD_BLOCK, "&e&lהכרזת הגרלה", "&7לחץ כדי להכריז על הגרלה חדשה לצופים."));
        }

        // 3. מצב היעלמות - Vanish (רצועה 12)
        if (player.hasPermission("media.vanish")) {
            gui.setItem(12, createGuiItem(Material.POTION, "&5&lמצב היעלמות (Vanish)", "&7הפעל/כבה מצב וניש לצורך צילום נקי."));
        }

        // 4. מצב טיסה - Fly (רצועה 13)
        if (player.hasPermission("media.fly")) {
            gui.setItem(13, createGuiItem(Material.FEATHER, "&b&lהפעלת טיסה", "&7ריחוף נוח לצילומי מסך וסרטונים."));
        }

        // 5. ריפוי והאכלה - Heal (רצועה 14)
        if (player.hasPermission("media.heal")) {
            gui.setItem(14, createGuiItem(Material.GOLDEN_APPLE, "&d&lרפואה מלאה והאכלה", "&7מלא חיים ושובע ברגע."));
        }

        // 6. ניקוי מזג אוויר (רצועה 15)
        if (player.hasPermission("media.weather")) {
            gui.setItem(15, createGuiItem(Material.SUNFLOWER, "&6&lשמש תמיד", "&7נקה את מזג האוויר ושמור על שמיים בהירים."));
        }

        // 7. שсет ליום (רצועה 16)
        if (player.hasPermission("media.time")) {
            gui.setItem(16, createGuiItem(Material.CLOCK, "&e&lקבע שעה ליום", "&7שеר זמנים ליום שמש מושלם לצילום."));
        }

        // 8. מצב אלמוות - God (רצועה 19)
        if (player.hasPermission("media.god")) {
            gui.setItem(19, createGuiItem(Material.NETHER_STAR, "&4&lמצב אלמוות (God)", "&7הגן על עצמך מפני נזק בזמן צילום."));
        }

        // 9. שידור הודעה כללית (רצועה 20)
        if (player.hasPermission("media.broadcast")) {
            gui.setItem(20, createGuiItem(Material.WRITABLE_BOOK, "&9&lשידור הודעה לשרת", "&7שלח הכרזה מיוחדת לכל הצופים בשרת."));
        }

        // 10. מצב הקלטה מיוחד (רצועה 21)
        if (player.hasPermission("media.recording")) {
            gui.setItem(21, createGuiItem(Material.ENDER_EYE, "&3&lמצב הקלטה נקי", "&7הסתרת כניסות/יציאות וניקוי צ'אט מהיר."));
        }

        player.openInventory(gui);
    }

    private static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.setLore(Arrays.asList(Arrays.stream(lore).map(s -> ChatColor.translateAlternateColorCodes('&', s)).toArray(String[]::new)));
            item.setItemMeta(meta);
        }
        return item;
    }
}
