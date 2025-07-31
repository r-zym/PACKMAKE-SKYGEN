package pl.okej.okejspaceskygengenerators.talismans;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;

public class TalismanManager {

    private final Main plugin;

    public TalismanManager(Main plugin) {
        this.plugin = plugin;
    }

    public double getTalismanMultiplier(Player player) {
        ItemStack offhandItem = player.getInventory().getItemInOffHand();

        if (TalismanItem.isTalisman(offhandItem)) {
            return TalismanItem.getTalismanMultiplier(offhandItem);
        }

        return 0.0;
    }

    public boolean hasTalisman(Player player) {
        return getTalismanMultiplier(player) > 0.0;
    }
}
