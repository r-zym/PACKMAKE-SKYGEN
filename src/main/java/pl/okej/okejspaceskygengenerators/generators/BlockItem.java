package pl.okej.okejspaceskygengenerators.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import pl.okej.okejspaceskygengenerators.Main;

import java.util.Collection;

public class BlockItem {

    public static final String BLOCK_KEY = "skygenerators.block";
    public static final String GENERATOR_KEY = "skygenerators.generator";
    public static final String SPAWN_TIME_KEY = "skygenerators.spawn_time";

    public static Item spawn(Location location, Material blockType, String generatorId, long spawnTime) {
        Location spawnLoc = location.clone();
        spawnLoc.setX(spawnLoc.getBlockX() + 0.5);
        spawnLoc.setY(spawnLoc.getBlockY() + 0.5);
        spawnLoc.setZ(spawnLoc.getBlockZ() + 0.5);

        ItemStack item = createBlockItem(blockType, generatorId, spawnTime);

        Item droppedItem = location.getWorld().dropItem(spawnLoc, item);
        droppedItem.setVelocity(new Vector(0, 0.1, 0));
        droppedItem.setGlowing(true);

        return droppedItem;
    }

    public static int countNearbyBlockItems(Location location, double radius, String generatorId) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
        int count = 0;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isBlockItem(item.getItemStack()) &&
                        generatorId.equals(getGeneratorId(item.getItemStack()))) {
                    count++;
                }
            }
        }

        return count;
    }

    public static void removeNearbyBlockItems(Location location, double radius, String generatorId) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isBlockItem(item.getItemStack()) &&
                        generatorId.equals(getGeneratorId(item.getItemStack()))) {
                    entity.remove();
                }
            }
        }
    }

    private static ItemStack createBlockItem(Material blockType, String generatorId, long spawnTime) {
        ItemStack item = new ItemStack(blockType);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), BLOCK_KEY),
                PersistentDataType.STRING,
                blockType.name()
        );

        meta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), GENERATOR_KEY),
                PersistentDataType.STRING,
                generatorId
        );

        meta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), SPAWN_TIME_KEY),
                PersistentDataType.LONG,
                spawnTime
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isBlockItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
                new NamespacedKey(Main.getInstance(), BLOCK_KEY),
                PersistentDataType.STRING
        );
    }

    public static Material getBlockType(ItemStack item) {
        if (!isBlockItem(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        String blockName = meta.getPersistentDataContainer().get(
                new NamespacedKey(Main.getInstance(), BLOCK_KEY),
                PersistentDataType.STRING
        );

        try {
            return Material.valueOf(blockName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String getGeneratorId(ItemStack item) {
        if (!isBlockItem(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(
                new NamespacedKey(Main.getInstance(), GENERATOR_KEY),
                PersistentDataType.STRING
        );
    }

    public static long getSpawnTime(ItemStack item) {
        if (!isBlockItem(item)) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(
                new NamespacedKey(Main.getInstance(), SPAWN_TIME_KEY),
                PersistentDataType.LONG,
                0L
        );
    }
}
