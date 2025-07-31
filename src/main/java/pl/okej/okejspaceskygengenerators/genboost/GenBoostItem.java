package pl.okej.okejspaceskygengenerators.genboost;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.okej.okejspaceskygengenerators.Main;

import java.util.ArrayList;
import java.util.List;

public class GenBoostItem {

    public static final String GENBOOST_KEY = "okejgenerators.genboost";

    public static ItemStack create(String id) {
        ConfigurationSection config = Main.getInstance().getConfigManager().getConfig()
                .getConfigurationSection("genboost_items." + id);

        if (config == null) {
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(config.getString("material", "BLAZE_POWDER").toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.BLAZE_POWDER;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Main.getInstance().getMessageUtils().formatMessage(
                config.getString("name", "&e&lGenerator Boost")
        ));

        List<String> lore = new ArrayList<>();
        for (String line : config.getStringList("lore")) {
            lore.add(Main.getInstance().getMessageUtils().formatMessage(line));
        }
        meta.setLore(lore);

        if (config.contains("custom_model_data")) {
            meta.setCustomModelData(config.getInt("custom_model_data"));
        }

        meta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), GENBOOST_KEY),
                PersistentDataType.STRING,
                id
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isGenBoostItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(Main.getInstance(), GENBOOST_KEY),
                PersistentDataType.STRING
        );
    }

    public static String getGenBoostId(ItemStack item) {
        if (!isGenBoostItem(item)) {
            return null;
        }

        return item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(Main.getInstance(), GENBOOST_KEY),
                PersistentDataType.STRING
        );
    }
}
