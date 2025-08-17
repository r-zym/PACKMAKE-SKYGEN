package pl.okej.okejspaceskygengenerators.generators;

import org.bukkit.configuration.ConfigurationSection;
import pl.okej.okejspaceskygengenerators.Main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GeneratorManager {

    private final Main plugin;
    private final Map<String, Generator> generators;

    public GeneratorManager(Main plugin) {
        this.plugin = plugin;
        this.generators = new HashMap<>();
    }

    public void loadGenerators() {
        generators.clear();

        ConfigurationSection generatorsSection = plugin.getConfigManager().getConfig().getConfigurationSection("generators");

        if (generatorsSection == null) {
            plugin.getLogger().warning(" Nie znaleziono żadnych generatorów w configu!");
            return;
        }

        for (String generatorId : generatorsSection.getKeys(false)) {
            ConfigurationSection generatorSection = generatorsSection.getConfigurationSection(generatorId);

            Generator generator = Generator.fromConfig(generatorId, generatorSection);

            if (generator != null) {
                generators.put(generatorId, generator);
                plugin.getLogger().info(" Załadowano generator: " + generatorId);
            }
        }

        plugin.getLogger().info(" Załadowano " + generators.size() + " generatorów!");
    }

    public void startGenerators() {
        generators.values().forEach(Generator::start);
        plugin.getLogger().info(" Wszystkie generatory są aktywne!");
    }

    public void stopGenerators() {
        generators.values().forEach(Generator::stop);
        plugin.getLogger().info(" Zatrzymuję działanie pluginu!");
    }

    public Generator getGenerator(String id) {
        return generators.get(id);
    }

    public Collection<Generator> getGenerators() {
        return generators.values();
    }

    public void toggleGenerator(String id) {
        Generator generator = getGenerator(id);
        if (generator != null) {
            generator.toggle();

            plugin.getConfigManager().getConfig().set("generators." + id + ".enabled", generator.isEnabled());
            plugin.getConfigManager().saveConfig();
        }
    }

    public void addGenerator(String id, Generator generator) {
        generators.put(id, generator);
    }
}