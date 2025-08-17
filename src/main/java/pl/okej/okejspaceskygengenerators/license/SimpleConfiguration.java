package pl.okej.okejspaceskygengenerators.license;

import org.bukkit.configuration.file.FileConfiguration;
import pl.okej.okejspaceskygengenerators.Main;

public class SimpleConfiguration {
    
    private final Main plugin;
    
    public SimpleConfiguration(Main plugin) {
        this.plugin = plugin;
    }
    
    public String getLicenseKey() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        return config.getString("license.key", "");
    }
    
    public String getCustomerId() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        return config.getString("license.customer_id", "");
    }
    
    public String getString(String path) {
        return plugin.getConfigManager().getConfig().getString(path);
    }
    
    public String getString(String path, String defaultValue) {
        return plugin.getConfigManager().getConfig().getString(path, defaultValue);
    }
}