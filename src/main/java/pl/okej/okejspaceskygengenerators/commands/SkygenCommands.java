package pl.okej.okejspaceskygengenerators.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Location;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.generators.Generator;
import pl.okej.okejspaceskygengenerators.generators.GeneratorType;
import pl.okej.okejspaceskygengenerators.genboost.GenBoostItem;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class SkygenCommands implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public SkygenCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("okejgenerators.admin")) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("no-permission")));
                    return true;
                }
                plugin.getConfigManager().reloadConfig();
                sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("reload")));
                return true;

            case "genboost":
                if (!sender.hasPermission("okejgenerators.admin")) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("no-permission")));
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cUżycie: /okejgenerators genboost <ilość> <czas>"));
                    return true;
                }
                try {
                    int multiplier = Integer.parseInt(args[1]);
                    int duration = Integer.parseInt(args[2]);
                    plugin.getGenBoostManager().startGenBoost(sender.getName(), multiplier, duration);
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cNieprawidłowe liczby!"));
                    return true;
                }

            case "nadaj":
                if (!sender.hasPermission("okejgenerators.admin")) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("no-permission")));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cTa komenda jest dostępna tylko dla graczy!"));
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cUżycie: /okejgenerators nadaj <nazwa>"));
                    return true;
                }
                Player player = (Player) sender;
                String itemId = args[1];
                ItemStack genBoostItem = GenBoostItem.create(itemId);
                if (genBoostItem == null) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cNie znaleziono przedmiotu o takiej nazwie!"));
                    return true;
                }
                player.getInventory().addItem(genBoostItem);
                return true;

            case "toggle":
                if (!sender.hasPermission("okejgenerators.admin")) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("no-permission")));
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cUżycie: /okejgenerators toggle <generator>"));
                    return true;
                }
                String generatorId = args[1];
                Generator generator = plugin.getGeneratorManager().getGenerator(generatorId);
                if (generator == null) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("generator.not_found")));
                    return true;
                }
                plugin.getGeneratorManager().toggleGenerator(generatorId);
                String message = generator.isEnabled() ?
                        plugin.getConfigManager().getMessage("generator.enabled") :
                        plugin.getConfigManager().getMessage("generator.disabled");
                sender.sendMessage(plugin.getMessageUtils().formatMessage(message.replace("%generator%", generatorId)));
                return true;

            case "list":
                if (!sender.hasPermission("okejgenerators.admin")) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("no-permission")));
                    return true;
                }
                sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("generator.list")));
                for (Generator gen : plugin.getGeneratorManager().getGenerators()) {
                    String status = gen.isEnabled() ? "&aWłączony" : "&cWyłączony";
                    String listEntry = plugin.getConfigManager().getMessage("generator.list_entry")
                            .replace("%generator%", gen.getId())
                            .replace("%status%", status);
                    sender.sendMessage(plugin.getMessageUtils().formatMessage(listEntry));
                }
                return true;
        }
        case "create":
        if (!sender.hasPermission("okejgenerators.admin")) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("no-permission")));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&cTa komenda jest dostępna tylko dla graczy!"));
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&cUżycie: /okejgenerators create <nazwa> <block/money> <blok/ilość> <czas>"));
            return true;
        }

        Player player = (Player) sender;
        String generatorName = args[1];
        String typeStr = args[2].toLowerCase();
        String valueStr = args[3];

        try {
            int interval = Integer.parseInt(args[4]);

            if (plugin.getGeneratorManager().getGenerator(generatorName) != null) {
                sender.sendMessage(plugin.getMessageUtils().formatMessage("&cGenerator o takiej nazwie już istnieje!"));
                return true;
            }

            Location playerLoc = player.getLocation();

            if (typeStr.equals("money")) {
                try {
                    double amount = Double.parseDouble(valueStr);
                    createMoneyGenerator(generatorName, playerLoc, amount, interval);
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&aUtworzono generator pieniędzy &e" + generatorName + " &ana pozycji &7" +
                            (int)playerLoc.getX() + ", " + (int)playerLoc.getY() + ", " + (int)playerLoc.getZ()));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cNieprawidłowa ilość pieniędzy!"));
                    return true;
                }
            } else if (typeStr.equals("block")) {
                try {
                    Material blockType = Material.valueOf(valueStr.toUpperCase());
                    createBlockGenerator(generatorName, playerLoc, blockType, interval);
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&aUtworzono generator bloków &e" + generatorName + " &ana pozycji &7" +
                            (int)playerLoc.getX() + ", " + (int)playerLoc.getY() + ", " + (int)playerLoc.getZ()));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cNieprawidłowy typ bloku!"));
                    return true;
                }
            } else {
                sender.sendMessage(plugin.getMessageUtils().formatMessage("&cTyp generatora musi być 'money' lub 'block'!"));
                return true;
            }

        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&cNieprawidłowy czas!"));
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&cUżycie: /okejgenerators create <nazwa> <block/money> <blok/ilość> <czas>"));
            return true;
        }
        return true;


        showHelp(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("okejgenerators.admin")) {
                completions.add("reload");
                completions.add("genboost");
                completions.add("nadaj");
                completions.add("create");
                completions.add("toggle");
                completions.add("list");
            }
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "nadaj":
                    if (sender.hasPermission("okejgenerators.admin")) {
                        completions.addAll(plugin.getConfigManager().getConfig().getConfigurationSection("genboost_items").getKeys(false));
                    }
                    break;
                case "toggle":
                    if (sender.hasPermission("okejgenerators.admin")) {
                        plugin.getGeneratorManager().getGenerators().forEach(gen -> completions.add(gen.getId()));
                    }
                    break;
                case "create":
                    if (sender.hasPermission("okejgenerators.admin")) {
                        if (args.length == 3) {
                            completions.addAll(Arrays.asList("money", "block"));
                        }
                    }
                    break;
            }
        }

        return completions;
    }

    private void showHelp(CommandSender sender) {
        String prefix = plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("prefix"));

        sender.sendMessage(plugin.getMessageUtils().formatMessage(prefix + "&x&0&0&8&D&F&F&lG&x&0&0&8&B&F&F&lE&x&0&0&8&A&F&F&lN&x&0&0&8&8&F&F&lE&x&0&0&8&6&F&F&lR&x&0&0&8&5&F&F&lA&x&0&0&8&3&F&F&lT&x&0&0&8&2&F&F&lO&x&0&0&8&0&F&F&lR&x&0&0&7&E&F&F&lY &x&0&0&7&B&F&F&lS&x&0&0&7&9&F&F&lK&x&0&0&7&8&F&F&lY&x&0&0&7&6&F&F&lG&x&0&0&7&4&F&F&lE&x&0&0&7&3&F&F&lN &x&0&0&6&F&F&F&lB&x&0&0&6&E&F&F&lY &x&0&0&6&B&F&F&lO&x&0&0&6&9&F&F&lK&x&0&0&6&7&F&F&l_&x&0&0&6&6&F&F&lE&x&0&0&6&4&F&F&lJ"));

        if (sender.hasPermission("okejgenerators.admin")) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen reload &7- &fPrzeładuj konfigurację pluginu"));
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen genboost <ilość> <czas> &7- &fAktywuj boost generatorów"));
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen nadaj <nazwa> &7- &fNadaj genboosta"));
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen create <nazwa> <block/money> <blok/ilość> <czas> &7- &fUtwórz generator"));
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen toggle <generator> &7- &fWłącz/wyłącz generator"));
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen list &7- &fLista generatorów"));
        }
    }

    private void createMoneyGenerator(String name, Location location, double amount, int interval) {
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.world", location.getWorld().getName());
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.x", location.getBlockX());
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.y", location.getBlockY());
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.z", location.getBlockZ());
        plugin.getConfigManager().getConfig().set("generators." + name + ".type", "money");
        plugin.getConfigManager().getConfig().set("generators." + name + ".interval", interval);
        plugin.getConfigManager().getConfig().set("generators." + name + ".amount", amount);
        plugin.getConfigManager().getConfig().set("generators." + name + ".enabled", true);
        plugin.getConfigManager().getConfig().set("generators." + name + ".allowed_protection_levels", Arrays.asList(1.0, 2.0));
        plugin.getConfigManager().getConfig().set("generators." + name + ".max_money_items", 2);
        plugin.getConfigManager().getConfig().set("generators." + name + ".money_item_lifetime", 900);
        plugin.getConfigManager().getConfig().set("generators." + name + ".items_per_interval", 1);
        plugin.getConfigManager().saveConfig();

        Generator generator = new Generator(name, location, interval, amount, null, GeneratorType.MONEY, true,
                Arrays.asList(1.0, 2.0), 2, 900, 1);
        plugin.getGeneratorManager().addGenerator(name, generator);
        generator.start();
    }

    private void createBlockGenerator(String name, Location location, Material blockType, int interval) {
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.world", location.getWorld().getName());
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.x", location.getBlockX());
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.y", location.getBlockY());
        plugin.getConfigManager().getConfig().set("generators." + name + ".location.z", location.getBlockZ());
        plugin.getConfigManager().getConfig().set("generators." + name + ".type", "block");
        plugin.getConfigManager().getConfig().set("generators." + name + ".block_type", blockType.name());
        plugin.getConfigManager().getConfig().set("generators." + name + ".interval", interval);
        plugin.getConfigManager().getConfig().set("generators." + name + ".enabled", true);
        plugin.getConfigManager().getConfig().set("generators." + name + ".allowed_protection_levels", Arrays.asList(1.0, 2.0));
        plugin.getConfigManager().getConfig().set("generators." + name + ".max_money_items", 2);
        plugin.getConfigManager().getConfig().set("generators." + name + ".money_item_lifetime", 900);
        plugin.getConfigManager().getConfig().set("generators." + name + ".items_per_interval", 1);
        plugin.getConfigManager().saveConfig();

        Generator generator = new Generator(name, location, interval, 0, blockType, GeneratorType.BLOCK, true,
                Arrays.asList(1.0, 2.0), 2, 900, 1);
        plugin.getGeneratorManager().addGenerator(name, generator);
        generator.start();
    }
}