package pl.okej.okejspaceskygengenerators.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.talismans.TalismanItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TalismanCommands implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public TalismanCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
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
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cUżycie: /packmake-talizmany nadaj <1/2/3>"));
                    return true;
                }

                try {
                    int level = Integer.parseInt(args[1]);
                    if (level < 1 || level > 3) {
                        sender.sendMessage(plugin.getMessageUtils().formatMessage("&cPoziom talizmanu musi być między 1 a 3!"));
                        return true;
                    }

                    Player player = (Player) sender;
                    ItemStack talisman = TalismanItem.create(level);

                    if (talisman == null) {
                        sender.sendMessage(plugin.getMessageUtils().formatMessage("&cNie można utworzyć talizmanu poziomu " + level + "!"));
                        return true;
                    }

                    player.getInventory().addItem(talisman);
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&aNadano talizman poziomu " + level + "!"));
                    return true;

                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessageUtils().formatMessage("&cPoziom talizmanu musi być liczbą!"));
                    return true;
                }

            default:
                showHelp(sender);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("okejgenerators.admin")) {
                completions.add("nadaj");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("nadaj")) {
            if (sender.hasPermission("okejgenerators.admin")) {
                completions.addAll(Arrays.asList("1", "2", "3"));
            }
        }

        return completions;
    }

    private void showHelp(CommandSender sender) {
        String prefix = plugin.getMessageUtils().formatMessage(plugin.getConfigManager().getMessage("prefix"));

        sender.sendMessage(plugin.getMessageUtils().formatMessage(prefix + "&x&F&F&D&7&0&0&lT&x&F&F&D&1&0&0&lA&x&F&F&C&B&0&0&lL&x&F&F&C&5&0&0&lI&x&F&F&B&F&0&0&lZ&x&F&F&B&9&0&0&lM&x&F&F&B&3&0&0&lA&x&F&F&A&D&0&0&lN&x&F&F&A&7&0&0&lY"));

        if (sender.hasPermission("okejgenerators.admin")) {
            sender.sendMessage(plugin.getMessageUtils().formatMessage("&8→ &9/packmake-talizmany nadaj <1/2/3> &7- &fNadaj talizman"));
        }
    }
}
