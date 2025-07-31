package pl.okej.okejspaceskygengenerators.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorUtils {

    public static double getAverageProtectionLevel(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        int totalProtection = 0;
        int armorPieces = 0;

        for (ItemStack piece : armor) {
            if (piece != null && piece.getEnchantments().containsKey(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                totalProtection += piece.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                armorPieces++;
            }
        }

        if (armorPieces == 0) {
            return 0.0;
        }

        return (double) totalProtection / armorPieces;
    }

    public static boolean hasRequiredProtectionLevel(Player player, double requiredLevel) {
        double averageProtection = getAverageProtectionLevel(player);
        return Math.abs(averageProtection - requiredLevel) < 0.01; // Allow for small floating point differences
    }
}
