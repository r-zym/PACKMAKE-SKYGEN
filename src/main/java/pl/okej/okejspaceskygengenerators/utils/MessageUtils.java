package pl.okej.okejspaceskygengenerators.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.generators.MoneyItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageUtils {

    private final Main plugin;
    private final Map<UUID, BossBar> bossBars;

    public MessageUtils(Main plugin) {
        this.plugin = plugin;
        this.bossBars = new HashMap<>();
    }

    public String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(Player player, String message) {
        String prefix = formatMessage(plugin.getConfigManager().getMessage("prefix"));
        player.sendMessage(formatMessage(prefix + message));
    }

    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(formatMessage(message)));
    }

    public void showBossBar(Player player, double amount) {
        if (!plugin.getConfigManager().getBoolean("bossbar.enabled", true)) {
            return;
        }

        String title = plugin.getConfigManager().getString("bossbar.title", "&aZebrano $%amount%");
        title = formatMessage(title.replace("%amount%", MoneyItem.formatMoney(amount)));

        String colorStr = plugin.getConfigManager().getString("bossbar.color", "GREEN").toUpperCase();
        String styleStr = plugin.getConfigManager().getString("bossbar.style", "SOLID").toUpperCase();
        int time = plugin.getConfigManager().getInt("bossbar.time", 5);

        BarColor color;
        try {
            color = BarColor.valueOf(colorStr);
        } catch (IllegalArgumentException e) {
            color = BarColor.GREEN;
        }

        BarStyle style;
        try {
            style = BarStyle.valueOf(styleStr);
        } catch (IllegalArgumentException e) {
            style = BarStyle.SOLID;
        }

        removeBossBar(player);

        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bossBar);

        Bukkit.getScheduler().runTaskLater(plugin, () -> removeBossBar(player), time * 20L);
    }

    public void removeBossBar(Player player) {
        BossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removePlayer(player);
            bossBar.setVisible(false);
        }
    }

    public void removeAllBossBars() {
        for (BossBar bossBar : bossBars.values()) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
        bossBars.clear();
    }
}
