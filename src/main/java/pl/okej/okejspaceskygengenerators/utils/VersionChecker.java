package pl.okej.okejspaceskygengenerators.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.okej.okejspaceskygengenerators.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionChecker implements Listener {
    
    private final Main plugin;
    private final String currentVersion;
    private final String versionCheckUrl;
    private String latestVersion;
    private boolean updateAvailable;
    
    public VersionChecker(Main plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.versionCheckUrl = "https://api.packmake.pl/version/skygen";
        this.updateAvailable = false;
        
        // Check for updates asynchronously
        checkForUpdates();
    }
    
    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(versionCheckUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    latestVersion = reader.readLine();
                    reader.close();
                    
                    if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                        updateAvailable = true;
                        
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.getLogger().info("════════════════════════════════════════════════════════");
                            plugin.getLogger().info("                    DOSTĘPNA AKTUALIZACJA");
                            plugin.getLogger().info("        Aktualna wersja: " + currentVersion);
                            plugin.getLogger().info("        Najnowsza wersja: " + latestVersion);
                            plugin.getLogger().info("        Pobierz z: https://packmake.pl/downloads");
                            plugin.getLogger().info("════════════════════════════════════════════════════════");
                        });
                    }
                }
                
            } catch (Exception e) {
                // Silently fail if we can't check for updates
                plugin.getLogger().warning("Nie można sprawdzić aktualizacji pluginu.");
            }
        });
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (updateAvailable && event.getPlayer().hasPermission("okejgenerators.admin")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                event.getPlayer().sendMessage(ColorUtil.colorize("&8[&6PackMake&8] &7Dostępna jest nowa wersja pluginu: &6" + latestVersion));
                event.getPlayer().sendMessage(ColorUtil.colorize("&8[&6PackMake&8] &7Pobierz z: &6https://packmake.pl/downloads"));
            }, 60L); // Wait 3 seconds after join
        }
    }
    
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
    public String getLatestVersion() {
        return latestVersion;
    }
    
    public String getCurrentVersion() {
        return currentVersion;
    }
}