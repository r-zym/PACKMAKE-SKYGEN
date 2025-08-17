package pl.okej.okejspaceskygengenerators.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.okej.okejspaceskygengenerators.Main;

public class MessageUtil {
    
    private final Main plugin;
    
    public MessageUtil(Main plugin) {
        this.plugin = plugin;
    }
    
    public void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        
        String prefix = plugin.getConfigManager().getMessage("prefix");
        sender.sendMessage(ColorUtil.colorize(prefix + message));
    }
    
    public void sendMessage(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        
        String prefix = plugin.getConfigManager().getMessage("prefix");
        player.sendMessage(ColorUtil.colorize(prefix + message));
    }
    
    public void sendMessageWithoutPrefix(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        
        sender.sendMessage(ColorUtil.colorize(message));
    }
    
    public void sendMessageWithoutPrefix(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        
        player.sendMessage(ColorUtil.colorize(message));
    }
    
    public void broadcast(String message) {
        if (message == null || message.isEmpty()) return;
        
        String prefix = plugin.getConfigManager().getMessage("prefix");
        Bukkit.broadcastMessage(ColorUtil.colorize(prefix + message));
    }
    
    public void broadcastWithoutPrefix(String message) {
        if (message == null || message.isEmpty()) return;
        
        Bukkit.broadcastMessage(ColorUtil.colorize(message));
    }
    
    public void sendActionBar(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(ColorUtil.colorize(message)));
    }
    
    public void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }
    
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(
                ColorUtil.colorize(title),
                ColorUtil.colorize(subtitle),
                fadeIn, stay, fadeOut
        );
    }
    
    public String format(String message) {
        return ColorUtil.colorize(message);
    }
}