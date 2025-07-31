package pl.okej.okejspaceskygengenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.generators.Generator;
import pl.okej.okejspaceskygengenerators.generators.MoneyItem;
import pl.okej.okejspaceskygengenerators.utils.ArmorUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyPickupListener implements Listener {

    private final Main plugin;
    private static final double PICKUP_RADIUS = 1.5;

    public MoneyPickupListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();

        if (MoneyItem.isMoneyItem(item)) {
            event.setCancelled(true);

            if (!player.hasPermission("okejgenerators.collect")) {
                plugin.getMessageUtils().sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
                return;
            }

            String generatorId = MoneyItem.getGeneratorId(item);
            if (generatorId == null) {
                return;
            }

            Generator generator = plugin.getGeneratorManager().getGenerator(generatorId);
            if (generator == null) {
                return;
            }

            if (!generator.getAllowedProtectionLevels().isEmpty()) {
                double playerProtectionLevel = ArmorUtils.getAverageProtectionLevel(player);
                boolean hasRequiredLevel = false;

                for (double allowedLevel : generator.getAllowedProtectionLevels()) {
                    if (Math.abs(playerProtectionLevel - allowedLevel) < 0.01) {
                        hasRequiredLevel = true;
                        break;
                    }
                }

                if (!hasRequiredLevel) {
                    String title = plugin.getConfigManager().getMessage("protection.insufficient_title");
                    String subtitle = plugin.getConfigManager().getMessage("protection.insufficient_subtitle")
                            .replace("%required%", generator.getAllowedProtectionLevels().toString())
                            .replace("%current%", String.format("%.1f", playerProtectionLevel));

                    player.sendTitle(
                            plugin.getMessageUtils().formatMessage(title),
                            plugin.getMessageUtils().formatMessage(subtitle),
                            0, 60, 20
                    );
                    return;
                }
            }

            double totalAmount = MoneyItem.getTotalMoneyNearby(event.getItem().getLocation(), PICKUP_RADIUS);
            MoneyItem.removeNearbyMoneyItems(event.getItem().getLocation(), PICKUP_RADIUS);
            collectMoney(player, totalAmount);
        }
    }

    private void collectMoney(Player player, double amount) {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedAmount = bd.doubleValue();

        plugin.getEconomy().depositPlayer(player, roundedAmount);

        String message = plugin.getConfigManager().getMessage("collect")
                .replace("%amount%", MoneyItem.formatMoney(roundedAmount));
        plugin.getMessageUtils().sendActionBar(player, message);

        plugin.getMessageUtils().showBossBar(player, roundedAmount);
    }
}