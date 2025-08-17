package pl.okej.okejspaceskygengenerators.generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import pl.okej.okejspaceskygengenerators.Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Generator {
    private static final double MONEY_ITEMS_CHECK_RADIUS = 3.0;

    private final String id;
    private final Location location;
    private final int interval;
    private final double amount;
    private final Material blockType;
    private final GeneratorType type;
    private final List<Double> allowedProtectionLevels;
    private final int maxMoneyItems;
    private final int moneyItemLifetime;
    private final int itemsPerInterval;
    private boolean enabled;
    private BukkitTask task;
    private BukkitTask cleanupTask;

    public static Generator fromConfig(String id, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        ConfigurationSection locationSection = section.getConfigurationSection("location");
        if (locationSection == null) {
            return null;
        }

        String worldName = locationSection.getString("world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            Main.getInstance().getLogger().warning("Świat '" + worldName + "' nie został znaleziony dla generatora '" + id + "'!");
            return null;
        }

        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        Location location = new Location(world, x, y, z);

        String typeStr = section.getString("type", "money").toUpperCase();
        GeneratorType type;
        try {
            type = GeneratorType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            type = GeneratorType.MONEY;
        }

        int interval = section.getInt("interval", 300);
        double amount = 0;
        Material blockType = null;

        if (type == GeneratorType.MONEY) {
            amount = section.getDouble("amount", 1000);
        } else if (type == GeneratorType.BLOCK) {
            String blockName = section.getString("block_type", "STONE");
            try {
                blockType = Material.valueOf(blockName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Main.getInstance().getLogger().warning("Nieprawidłowy typ bloku '" + blockName + "' dla generatora '" + id + "'!");
                blockType = Material.STONE;
            }
        }

        boolean enabled = section.getBoolean("enabled", true);
        int maxMoneyItems = section.getInt("max_money_items", 10);
        int moneyItemLifetime = section.getInt("money_item_lifetime", 300);
        int itemsPerInterval = section.getInt("items_per_interval", 1);

        List<Double> allowedProtectionLevels = new ArrayList<>();
        if (section.contains("allowed_protection_levels")) {
            for (Object level : section.getList("allowed_protection_levels")) {
                if (level instanceof Number) {
                    allowedProtectionLevels.add(((Number) level).doubleValue());
                } else if (level instanceof String) {
                    try {
                        allowedProtectionLevels.add(Double.parseDouble((String) level));
                    } catch (NumberFormatException e) {
                        Main.getInstance().getLogger().warning("Nieprawidłowy poziom ochrony '" + level + "' dla generatora '" + id + "'!");
                    }
                }
            }
        }

        return new Generator(id, location, interval, amount, blockType, type, enabled, allowedProtectionLevels, maxMoneyItems, moneyItemLifetime, itemsPerInterval);
    }

    public Generator(String id, Location location, int interval, double amount, Material blockType, GeneratorType type, boolean enabled,
                     List<Double> allowedProtectionLevels, int maxMoneyItems, int moneyItemLifetime, int itemsPerInterval) {
        this.id = id;
        this.location = location;
        this.interval = interval;
        this.amount = amount;
        this.blockType = blockType;
        this.type = type;
        this.enabled = enabled;
        this.allowedProtectionLevels = allowedProtectionLevels;
        this.maxMoneyItems = maxMoneyItems;
        this.moneyItemLifetime = moneyItemLifetime;
        this.itemsPerInterval = itemsPerInterval;
    }

    public void start() {
        if (!enabled) {
            return;
        }

        stop();

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            generateMoney();
        }, 20L, interval * 20L);

        cleanupTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            cleanupOldItems();
        }, 20L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
    }

    private int countNearbyItemsFromThisGenerator() {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(
                location,
                MONEY_ITEMS_CHECK_RADIUS,
                MONEY_ITEMS_CHECK_RADIUS,
                MONEY_ITEMS_CHECK_RADIUS
        );
        int count = 0;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (type == GeneratorType.MONEY) {
                    if (MoneyItem.isMoneyItem(item.getItemStack()) &&
                            id.equals(MoneyItem.getGeneratorId(item.getItemStack()))) {
                        count++;
                    }
                } else if (type == GeneratorType.BLOCK) {
                    if (BlockItem.isBlockItem(item.getItemStack()) &&
                            id.equals(BlockItem.getGeneratorId(item.getItemStack()))) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public void generateMoney() {
        if (!enabled) {
            return;
        }

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return;
        }

        int currentItems = countNearbyItemsFromThisGenerator();

        if (currentItems >= maxMoneyItems) {
            return;
        }

        int itemsToSpawn = Math.min(itemsPerInterval, maxMoneyItems - currentItems);
        if (itemsToSpawn <= 0) {
            return;
        }

        for (int i = 0; i < itemsToSpawn; i++) {
            if (type == GeneratorType.MONEY) {
                double finalAmount = amount;
                if (Main.getInstance().getGenBoostManager().isActive()) {
                    finalAmount *= Main.getInstance().getGenBoostManager().getMultiplier();
                }
                MoneyItem.spawn(location, finalAmount, id, System.currentTimeMillis());
            } else if (type == GeneratorType.BLOCK) {
                BlockItem.spawn(location, blockType, id, System.currentTimeMillis());
            }
        }
    }

    private void cleanupOldItems() {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(
                location,
                MONEY_ITEMS_CHECK_RADIUS,
                MONEY_ITEMS_CHECK_RADIUS,
                MONEY_ITEMS_CHECK_RADIUS
        );
        long currentTime = System.currentTimeMillis();

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;

                if (type == GeneratorType.MONEY && MoneyItem.isMoneyItem(item.getItemStack())) {
                    String itemGeneratorId = MoneyItem.getGeneratorId(item.getItemStack());
                    if (id.equals(itemGeneratorId)) {
                        long spawnTime = MoneyItem.getSpawnTime(item.getItemStack());
                        if (spawnTime > 0 && (currentTime - spawnTime) / 1000 >= moneyItemLifetime) {
                            entity.remove();
                        }
                    }
                } else if (type == GeneratorType.BLOCK && BlockItem.isBlockItem(item.getItemStack())) {
                    String itemGeneratorId = BlockItem.getGeneratorId(item.getItemStack());
                    if (id.equals(itemGeneratorId)) {
                        long spawnTime = BlockItem.getSpawnTime(item.getItemStack());
                        if (spawnTime > 0 && (currentTime - spawnTime) / 1000 >= moneyItemLifetime) {
                            entity.remove();
                        }
                    }
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getInterval() {
        return interval;
    }

    public double getAmount() {
        return amount;
    }

    public Material getBlockType() {
        return blockType;
    }

    public GeneratorType getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Double> getAllowedProtectionLevels() {
        return allowedProtectionLevels;
    }

    public int getMaxMoneyItems() {
        return maxMoneyItems;
    }

    public int getMoneyItemLifetime() {
        return moneyItemLifetime;
    }

    public int getItemsPerInterval() {
        return itemsPerInterval;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            start();
        } else {
            stop();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }
}