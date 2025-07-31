package pl.okej.okejspaceskygengenerators.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.generators.Generator;
import pl.okej.okejspaceskygengenerators.genboost.GenBoostItem;

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
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen toggle <generator> &7- &fWłącz/wyłącz generator"));
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/okgen list &7- &fLista generatorów"));
        }
    }
}