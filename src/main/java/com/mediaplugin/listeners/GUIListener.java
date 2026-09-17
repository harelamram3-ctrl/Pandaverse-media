package com.mediaplugin.listeners;

import com.mediaplugin.MediaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private final MediaPlugin plugin;

    public GUIListener(MediaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Pandaverse Media Hub")) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            switch (itemName) {
                case "הכרזת לייב":
                    if (player.hasPermission("media.live")) {
                        player.closeInventory();
                        player.performCommand("broadcast &8[&b&lPandaverse&f&lMedia&8] &cהיוטיובר/סטרימר &e" + player.getName() + " &cכרגע בלייב! בואו לצפות בו!");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                    break;
                case "הכרזת הגרלה":
                    if (player.hasPermission("media.giveaway")) {
                        player.closeInventory();
                        player.performCommand("broadcast &8[&b&lPandaverse&f&lMedia&8] &e&lהגרלה חדשה מתחילה עכשיו אצל &c" + player.getName() + "!");
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    }
                    break;
                case "מצב היעלמות (Vanish)":
                    if (player.hasPermission("media.vanish")) {
                        player.sendMessage(ChatColor.AQUA + "מצב היעלמות (Vanish) שונה בהצלחה.");
                        // ניתן לחבר כאן פלאגין וניש או לוגיקת התינוק שלך
                    }
                    break;
                case "הפעלת טיסה":
                    if (player.hasPermission("media.fly")) {
                        boolean newFly = !player.isFlying();
                        player.setAllowFlight(true);
                        player.setFlying(newFly);
                        player.sendMessage(ChatColor.AQUA + "מצב טיסה: " + (newFly ? "מופעל" : "כבוי"));
                    }
                    break;
                case "רפואה מלאה והאכלה":
                    if (player.hasPermission("media.heal")) {
                        player.setHealth(20.0);
                        player.setFoodLevel(20);
                        player.sendMessage(ChatColor.GREEN + "בריאות ושובע מלאים!");
                    }
                    break;
                case "שמש תמיד":
                    if (player.hasPermission("media.weather")) {
                        player.getWorld().setStorm(false);
                        player.getWorld().setThundering(false);
                        player.sendMessage(ChatColor.YELLOW + "מזג האוויר נוקה לשמש יפה.");
                    }
                    break;
                case "קבע שעה ליום":
                    if (player.hasPermission("media.time")) {
                        player.getWorld().setTime(1000);
                        player.sendMessage(ChatColor.YELLOW + "השעה שונתה לבוקר/יום.");
                    }
                    break;
                case "מצב אלמוות (God)":
                    if (player.hasPermission("media.god")) {
                        boolean current = player.isInvulnerable();
                        player.setInvulnerable(!current);
                        player.sendMessage(ChatColor.RED + "מצב אלמוות (God): " + (!current ? "מופעל" : "כבוי"));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
