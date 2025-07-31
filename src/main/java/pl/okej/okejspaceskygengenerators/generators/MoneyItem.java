package pl.okej.okejspaceskygengenerators.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import pl.okej.okejspaceskygengenerators.Main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;

public class MoneyItem {

    public static final String MONEY_KEY = "skygenerators.money";
    public static final String GENERATOR_KEY = "skygenerators.generator";
    public static final String SPAWN_TIME_KEY = "skygenerators.spawn_time";
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#0.00");

    public static Item spawn(Location location, double amount, String generatorId, long spawnTime) {
        Location spawnLoc = location.clone();
        spawnLoc.setX(spawnLoc.getBlockX() + 0.5);
        spawnLoc.setY(spawnLoc.getBlockY() + 0.5);
        spawnLoc.setZ(spawnLoc.getBlockZ() + 0.5);

        ItemStack item = createMoneyItem(amount, generatorId, spawnTime);

        Item droppedItem = location.getWorld().dropItem(spawnLoc, item);
        droppedItem.setVelocity(new Vector(0, 0.1, 0));
        droppedItem.setGlowing(true);

        return droppedItem;
    }

    public static double getTotalMoneyNearby(Location location, double radius) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
        double total = 0;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isMoneyItem(item.getItemStack())) {
                    total += getMoneyAmount(item.getItemStack());
                }
            }
        }

        return roundMoney(total);
    }

    public static void removeNearbyMoneyItems(Location location, double radius) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isMoneyItem(item.getItemStack())) {
                    entity.remove();
                }
            }
        }
    }

    private static ItemStack createMoneyItem(double amount, String generatorId, long spawnTime) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        int customModelData = Main.getInstance().getConfigManager().getInt("item.custom-model-data", 10000);
        meta.setCustomModelData(customModelData);

        meta.getPersistentDataContainer().set(
                org.bukkit.NamespacedKey.fromString(MONEY_KEY, Main.getInstance()),
                PersistentDataType.DOUBLE,
                amount
        );

        meta.getPersistentDataContainer().set(
                org.bukkit.NamespacedKey.fromString(GENERATOR_KEY, Main.getInstance()),
                PersistentDataType.STRING,
                generatorId
        );

        meta.getPersistentDataContainer().set(
                org.bukkit.NamespacedKey.fromString(SPAWN_TIME_KEY, Main.getInstance()),
                PersistentDataType.LONG,
                spawnTime
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isMoneyItem(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
                org.bukkit.NamespacedKey.fromString(MONEY_KEY, Main.getInstance()),
                PersistentDataType.DOUBLE
        );
    }

    public static double getMoneyAmount(ItemStack item) {
        if (!isMoneyItem(item)) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        double amount = meta.getPersistentDataContainer().getOrDefault(
                org.bukkit.NamespacedKey.fromString(MONEY_KEY, Main.getInstance()),
                PersistentDataType.DOUBLE,
                0.0
        );

        return roundMoney(amount);
    }

    public static String getGeneratorId(ItemStack item) {
        if (!isMoneyItem(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(
                org.bukkit.NamespacedKey.fromString(GENERATOR_KEY, Main.getInstance()),
                PersistentDataType.STRING
        );
    }

    public static long getSpawnTime(ItemStack item) {
        if (!isMoneyItem(item)) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(
                org.bukkit.NamespacedKey.fromString(SPAWN_TIME_KEY, Main.getInstance()),
                PersistentDataType.LONG,
                0L
        );
    }

    private static double roundMoney(double amount) {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String formatMoney(double amount) {
        amount = roundMoney(amount);

        if (amount == Math.floor(amount)) {
            return String.valueOf((int) amount);
        }

        String formatted = MONEY_FORMAT.format(amount);

        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0+$", "").replaceAll("\\.$", "");
        }

        return formatted;
    }
}