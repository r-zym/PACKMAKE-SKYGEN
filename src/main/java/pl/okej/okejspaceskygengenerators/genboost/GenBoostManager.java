package pl.okej.okejspaceskygengenerators.genboost;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitTask;
import pl.okej.okejspaceskygengenerators.Main;

public class GenBoostManager {

    private final Main plugin;
    private BukkitTask genBoostTask;
    private BossBar bossBar;
    private int timeLeft;
    private int multiplier;
    private boolean active;

    public GenBoostManager(Main plugin) {
        this.plugin = plugin;
        this.active = false;
    }

    public void startGenBoost(String activator, int multiplier, int duration) {
        if (active) {
            stopGenBoost();
        }

        this.multiplier = multiplier;
        this.timeLeft = duration;
        this.active = true;

        String title = plugin.getConfigManager().getMessage("genboost.activated");
        String subtitle = plugin.getConfigManager().getMessage("genboost.activated_subtitle")
                .replace("%player%", activator)
                .replace("%time%", String.valueOf(duration))
                .replace("%multiplier%", String.valueOf(multiplier));

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(
                    plugin.getMessageUtils().formatMessage(title),
                    plugin.getMessageUtils().formatMessage(subtitle),
                    10, 70, 20
            );
        });

        bossBar = Bukkit.createBossBar(
                plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("genboost.bossbar")
                        .replace("%player%", activator)
                        .replace("%time%", String.valueOf(timeLeft))
                        .replace("%multiplier%", String.valueOf(multiplier))),
                BarColor.GREEN,
                BarStyle.SOLID
        );

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        genBoostTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            timeLeft--;

            if (timeLeft <= 0) {
                stopGenBoost();
                return;
            }

            bossBar.setTitle(plugin.getMessageUtils().formatMessage(
                    plugin.getConfigManager().getMessage("genboost.bossbar")
                            .replace("%player%", activator)
                            .replace("%time%", String.valueOf(timeLeft))
                            .replace("%multiplier%", String.valueOf(multiplier))
            ));
        }, 20L, 20L);
    }

    public void stopGenBoost() {
        if (!active) {
            return;
        }

        if (genBoostTask != null) {
            genBoostTask.cancel();
            genBoostTask = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        String title = plugin.getConfigManager().getMessage("genboost.ended");
        String subtitle = plugin.getConfigManager().getMessage("genboost.ended_subtitle");

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(
                    plugin.getMessageUtils().formatMessage(title),
                    plugin.getMessageUtils().formatMessage(subtitle),
                    10, 70, 20
            );
        });

        active = false;
        multiplier = 1;
    }

    public boolean isActive() {
        return active;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
