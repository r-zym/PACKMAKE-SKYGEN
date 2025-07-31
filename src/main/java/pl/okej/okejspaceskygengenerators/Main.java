package pl.okej.okejspaceskygengenerators;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.okej.okejspaceskygengenerators.commands.SkygenCommands;
import pl.okej.okejspaceskygengenerators.config.ConfigManager;
import pl.okej.okejspaceskygengenerators.generators.GeneratorManager;
import pl.okej.okejspaceskygengenerators.listeners.MoneyPickupListener;
import pl.okej.okejspaceskygengenerators.listeners.GenBoostListener;
import pl.okej.okejspaceskygengenerators.genboost.GenBoostManager;
import pl.okej.okejspaceskygengenerators.utils.MessageUtils;

public final class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private GeneratorManager generatorManager;
    private MessageUtils messageUtils;
    private Economy economy;
    private GenBoostManager genBoostManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        if (!setupEconomy()) {
            getLogger().info("");
            getLogger().info("__________________________________________________________");
            getLogger().info("");
            getLogger().info("             A-PACKMAKE-SKYGEN [1.0-SNAPSHOT]");
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
        genBoostManager = new GenBoostManager(this);

        generatorManager = new GeneratorManager(this);
        generatorManager.loadGenerators();
        generatorManager.startGenerators();

        getServer().getPluginManager().registerEvents(new MoneyPickupListener(this), this);
        getServer().getPluginManager().registerEvents(new GenBoostListener(this), this);

        getCommand("okejgenerators").setExecutor(new SkygenCommands(this));

        getLogger().info("");
        getLogger().info("__________________________________________________________");
        getLogger().info("");
        getLogger().info("             A-PACKMAKE-SKYGEN [1.0-SNAPSHOT]");
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
        getLogger().info("             A-PACKMAKE-SKYGEN [1.0-SNAPSHOT]");
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
}