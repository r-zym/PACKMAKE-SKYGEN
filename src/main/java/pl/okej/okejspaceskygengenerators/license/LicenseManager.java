package pl.okej.okejspaceskygengenerators.license;

import org.bukkit.Bukkit;
import pl.okej.okejspaceskygengenerators.Main;

public class LicenseManager {
    
    private final Main plugin;
    private final SimpleConfiguration config;
    private boolean licenseValid;
    
    public LicenseManager(Main plugin) {
        this.plugin = plugin;
        this.config = new SimpleConfiguration(plugin);
        this.licenseValid = false;
    }
    
    public boolean verifyLicense() {
        String licenseKey = config.getLicenseKey();
        String customerId = config.getCustomerId();
        
        if (licenseKey.isEmpty() || customerId.isEmpty()) {
            plugin.getLogger().warning("╔════════════════════════════════════════════════════════╗");
            plugin.getLogger().warning("║                    BŁĄD LICENCJI                       ║");
            plugin.getLogger().warning("║           BRAK KLUCZA LICENCJI W CONFIG.YML            ║");
            plugin.getLogger().warning("║         Skontaktuj się z support@packmake.pl           ║");
            plugin.getLogger().warning("╚════════════════════════════════════════════════════════╝");
            return false;
        }
        
        // Verify license with server
        boolean serverResponse = LicenseServerUtil.verifyLicense(licenseKey, customerId);
        
        if (serverResponse) {
            plugin.getLogger().info("╔════════════════════════════════════════════════════════╗");
            plugin.getLogger().info("║                  LICENCJA SPRAWDZONA                   ║");
            plugin.getLogger().info("║                    PLUGIN AKTYWNY                      ║");
            plugin.getLogger().info("╚════════════════════════════════════════════════════════╝");
            licenseValid = true;
            return true;
        } else {
            String response = LicenseServerUtil.getServerResponse(licenseKey, customerId);
            plugin.getLogger().severe("╔════════════════════════════════════════════════════════╗");
            plugin.getLogger().severe("║                    BŁĄD LICENCJI                       ║");
            plugin.getLogger().severe("║                NIEPRAWIDŁOWA LICENCJA                  ║");
            plugin.getLogger().severe("║         Skontaktuj się z support@packmake.pl           ║");
            plugin.getLogger().severe("║              Odpowiedź serwera: " + response + "                ║");
            plugin.getLogger().severe("╚════════════════════════════════════════════════════════╝");
            return false;
        }
    }
    
    public boolean isLicenseValid() {
        return licenseValid;
    }
    
    public void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(plugin);
    }
}