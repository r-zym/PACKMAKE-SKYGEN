package pl.okej.okejspaceskygengenerators.talismans;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.okej.okejspaceskygengenerators.Main;

import java.util.ArrayList;
import java.util.List;

public class TalismanItem {

    public static final String TALISMAN_KEY = "okejgenerators.talisman";

    public static ItemStack create(int level) {
        ConfigurationSection config = Main.getInstance().getConfigManager().getConfig()
                .getConfigurationSection("talismans." + level);

        if (config == null) {
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(config.getString("material", "NETHER_STAR").toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.NETHER_STAR;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String name = config.getString("name", "&6Talizman " + level);
        meta.setDisplayName(Main.getInstance().getMessageUtils().formatMessage(name));

        List<String> lore = new ArrayList<>();
        for (String line : config.getStringList("lore")) {
            String formattedLine = line.replace("%multiplier%", String.valueOf(config.getDouble("multiplier", 0.5)));
            lore.add(Main.getInstance().getMessageUtils().formatMessage(formattedLine));
        }
        meta.setLore(lore);

        if (config.contains("custom_model_data")) {
            meta.setCustomModelData(config.getInt("custom_model_data"));
        }

        if (config.contains("enchantments")) {
            ConfigurationSection enchantments = config.getConfigurationSection("enchantments");
            if (enchantments != null) {
                for (String enchantName : enchantments.getKeys(false)) {
                    try {
                        Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase());
                        if (enchantment != null) {
                            int enchantLevel = enchantments.getInt(enchantName);
                            meta.addEnchant(enchantment, enchantLevel, true);
                        }
                    } catch (Exception e) {
                        Main.getInstance().getLogger().warning("Nieprawidłowy enchant: " + enchantName);
                    }
                }
            }
        }

        if (config.contains("flags")) {
            List<String> flags = config.getStringList("flags");
            for (String flagName : flags) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
                    meta.addItemFlags(flag);
                } catch (IllegalArgumentException e) {
                    Main.getInstance().getLogger().warning("Nieprawidłowa flaga: " + flagName);
                }
            }
        }

        meta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), TALISMAN_KEY),
                PersistentDataType.INTEGER,
                level
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isTalisman(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(Main.getInstance(), TALISMAN_KEY),
                PersistentDataType.INTEGER
        );
    }

    public static int getTalismanLevel(ItemStack item) {
        if (!isTalisman(item)) {
            return 0;
        }

        return item.getItemMeta().getPersistentDataContainer().getOrDefault(
                new NamespacedKey(Main.getInstance(), TALISMAN_KEY),
                PersistentDataType.INTEGER,
                0
        );
    }

    public static double getTalismanMultiplier(ItemStack item) {
        if (!isTalisman(item)) {
            return 0.0;
        }

        int level = getTalismanLevel(item);
        ConfigurationSection config = Main.getInstance().getConfigManager().getConfig()
                .getConfigurationSection("talismans." + level);

        if (config == null) {
            return 0.0;
        }

        return config.getDouble("multiplier", 0.0);
    }
}