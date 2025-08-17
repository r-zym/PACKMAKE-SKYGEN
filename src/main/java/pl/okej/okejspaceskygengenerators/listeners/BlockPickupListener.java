package pl.okej.okejspaceskygengenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.generators.BlockItem;
import pl.okej.okejspaceskygengenerators.generators.Generator;
import pl.okej.okejspaceskygengenerators.utils.ArmorUtils;

public class BlockPickupListener implements Listener {

    private final Main plugin;
    private static final double PICKUP_RADIUS = 1.5;

    public BlockPickupListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();

        if (BlockItem.isBlockItem(item)) {
            event.setCancelled(true);

            if (!player.hasPermission("okejgenerators.collect")) {
                plugin.getMessageUtils().sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
                return;
            }

            String generatorId = BlockItem.getGeneratorId(item);
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

            collectBlocks(player, event.getItem().getLocation(), generatorId);
        }
    }

    private void collectBlocks(Player player, org.bukkit.Location location, String generatorId) {
        int blocksCollected = BlockItem.countNearbyBlockItems(location, PICKUP_RADIUS, generatorId);

        if (blocksCollected > 0) {
            Generator generator = plugin.getGeneratorManager().getGenerator(generatorId);
            if (generator != null && generator.getBlockType() != null) {
                ItemStack blockStack = new ItemStack(generator.getBlockType(), blocksCollected);
                player.getInventory().addItem(blockStack);

                BlockItem.removeNearbyBlockItems(location, PICKUP_RADIUS, generatorId);

                String message = "&a+%amount% %block%"
                        .replace("%amount%", String.valueOf(blocksCollected))
                        .replace("%block%", generator.getBlockType().name().toLowerCase().replace("_", " "));
                plugin.getMessageUtils().sendActionBar(player, message);
            }
        }
    }
}
