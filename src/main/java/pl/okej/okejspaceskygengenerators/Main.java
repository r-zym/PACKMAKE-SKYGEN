package pl.okej.okejspaceskygengenerators;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.okej.okejspaceskygengenerators.commands.SkygenCommands;
import pl.okej.okejspaceskygengenerators.commands.TalismanCommands;
import pl.okej.okejspaceskygengenerators.config.ConfigManager;
import pl.okej.okejspaceskygengenerators.generators.GeneratorManager;
import pl.okej.okejspaceskygengenerators.license.LicenseManager;
import pl.okej.okejspaceskygengenerators.listeners.MoneyPickupListener;
import pl.okej.okejspaceskygengenerators.listeners.GenBoostListener;
import pl.okej.okejspaceskygengenerators.genboost.GenBoostManager;
import pl.okej.okejspaceskygengenerators.talismans.TalismanManager;
import pl.okej.okejspaceskygengenerators.utils.MessageUtils;
import pl.okej.okejspaceskygengenerators.utils.MessageUtil;
import pl.okej.okejspaceskygengenerators.utils.VersionChecker;

public final class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private GeneratorManager generatorManager;
    private MessageUtils messageUtils;
    private MessageUtil messageUtil;
    private Economy economy;
    private GenBoostManager genBoostManager;
    private TalismanManager talismanManager;
    private LicenseManager licenseManager;
    private VersionChecker versionChecker;

    @Override
    public void onEnable() {
        instance = this;

        // Plugin.yml protection check
        if (!this.getDescription().getAuthors().contains("ok_ej for PackMake") || 
            !this.getDescription().getWebsite().equalsIgnoreCase("dc.packmake.pl")) {
            this.getLogger().severe("╔════════════════════════════════════════════════════════╗");
            this.getLogger().severe("║                        BŁĄD                            ║");
            this.getLogger().severe("║               NIE EDYTUJ PLUGIN.YML                    ║");
            this.getLogger().severe("╚════════════════════════════════════════════════════════╝");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize license system
        licenseManager = new LicenseManager(this);
        if (!licenseManager.verifyLicense()) {
            licenseManager.disablePlugin();
            return;
        }

        if (!setupEconomy()) {
            getLogger().info("");
            getLogger().info("__________________________________________________________");
            getLogger().info("");
            getLogger().info("             A-PACKMAKE-SKYGEN [1.1-SNAPSHOT]");
            getLogger().info("                      Autor: ok_ej");
            getLogger().info("             Discord: https://dc.packmake.pl");
            getLogger().info("");
            getLogger().info("         NIE ZNALEZIONO PLUGINU VAULT LUB ESSENTIALSX");
            getLogger().info("                     Pobierz go tutaj:");
            getLogger().info("        https://www.spigotmc.org/resources/vault.34315/");
            getLogger().info("");
            getLogger().info("__________________________________________________________");
            getLogger().info("");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        messageUtils = new MessageUtils(this);
        messageUtil = new MessageUtil(this);
        genBoostManager = new GenBoostManager(this);
        talismanManager = new TalismanManager(this);

        // Initialize version checker
        versionChecker = new VersionChecker(this);
        getServer().getPluginManager().registerEvents(versionChecker, this);

        generatorManager = new GeneratorManager(this);
        generatorManager.loadGenerators();
        generatorManager.startGenerators();

        getServer().getPluginManager().registerEvents(new MoneyPickupListener(this), this);
        getServer().getPluginManager().registerEvents(new GenBoostListener(this), this);
        getServer().getPluginManager().registerEvents(new pl.okej.okejspaceskygengenerators.listeners.BlockPickupListener(this), this);

        getCommand("okejgenerators").setExecutor(new SkygenCommands(this));
        getCommand("packmake-talizmany").setExecutor(new TalismanCommands(this));

        getLogger().info("");
        getLogger().info("__________________________________________________________");
        getLogger().info("");
        getLogger().info("             A-PACKMAKE-SKYGEN [1.1-SNAPSHOT]");
        getLogger().info("                      Autor: ok_ej");
        getLogger().info("             Discord: https://dc.packmake.pl");
        getLogger().info("");
        getLogger().info("           PLUGIN ZOSTAŁ POMYŚLNIE URUCHOMIONY!");
        getLogger().info("");
        getLogger().info("__________________________________________________________");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        if (generatorManager != null) {
            generatorManager.stopGenerators();
        }

        if (genBoostManager != null && genBoostManager.isActive()) {
            genBoostManager.stopGenBoost();
        }

        getLogger().info("");
        getLogger().info("__________________________________________________________");
        getLogger().info("");
        getLogger().info("             A-PACKMAKE-SKYGEN [1.1-SNAPSHOT]");
        getLogger().info("                      Autor: ok_ej");
        getLogger().info("             Discord: https://dc.packmake.pl");
        getLogger().info("");
        getLogger().info("             PLUGIN ZOSTAŁ POMYŚLNIE ZATRZYMANY!");
        getLogger().info("");
        getLogger().info("__________________________________________________________");
        getLogger().info("");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public MessageUtils getMessageUtils() {
        return messageUtils;
    }

    public Economy getEconomy() {
        return economy;
    }

    public GenBoostManager getGenBoostManager() {
        return genBoostManager;
    }

    public TalismanManager getTalismanManager() {
        return talismanManager;
    }

    public LicenseManager getLicenseManager() {
        return licenseManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public VersionChecker getVersionChecker() {
        return versionChecker;
    }
}