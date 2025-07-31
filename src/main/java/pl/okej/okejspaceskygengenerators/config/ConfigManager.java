package pl.okej.okejspaceskygengenerators.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.okej.okejspaceskygengenerators.Main;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("");
        plugin.getLogger().info(" PLUGIN OKEJSPACE-SKYGENGENERATORS");
        plugin.getLogger().info(" Konfiguracja pluginu została załadowana!");
        plugin.getLogger().info(" Discord: https://dc.okej.space");
        plugin.getLogger().info("");
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("");
        plugin.getLogger().info(" PLUGIN OKEJSPACE-SKYGENGENERATORS");
        plugin.getLogger().info(" Konfiguracja pluginu została przeładowana!");
        plugin.getLogger().info(" Discord: https://dc.okej.space");
        plugin.getLogger().info("");

        // Reload generators
        plugin.getGeneratorManager().stopGenerators();
        plugin.getGeneratorManager().loadGenerators();
        plugin.getGeneratorManager().startGenerators();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie można zapisać konfiguracji do pliku " + configFile);
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "Wiadomości nie znaleziono: " + path);
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public double getDouble(String path, double defaultValue) {
        return config.getDouble(path, defaultValue);
    }
}
