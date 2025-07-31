package pl.okej.okejspaceskygengenerators.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.genboost.GenBoostItem;

public class GenBoostListener implements Listener {

    private final Main plugin;

    public GenBoostListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !GenBoostItem.isGenBoostItem(item)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        String genBoostId = GenBoostItem.getGenBoostId(item);

        ConfigurationSection config = plugin.getConfigManager().getConfig()
                .getConfigurationSection("genboost_items." + genBoostId);

        if (config == null) {
            return;
        }

        int multiplier = config.getInt("multiplier", 2);
        int duration = config.getInt("duration", 30);

        item.setAmount(item.getAmount() - 1);

        plugin.getGenBoostManager().startGenBoost(player.getName(), multiplier, duration);
    }
}
