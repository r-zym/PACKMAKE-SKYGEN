package pl.okej.okejspaceskygengenerators.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    
    public static String colorize(String message) {
        if (message == null) return "";
        
        // Handle hex colors &#RRGGBB
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String color = matcher.group(1);
            message = message.replace("&#" + color, net.md_5.bungee.api.ChatColor.of("#" + color).toString());
        }
        
        // Handle standard color codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static String stripColor(String message) {
        if (message == null) return "";
        return ChatColor.stripColor(message);
    }
    
    public static String translateHexColorCodes(String message) {
        if (message == null) return "";
        
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String color = matcher.group(1);
            message = message.replace("&#" + color, net.md_5.bungee.api.ChatColor.of("#" + color).toString());
        }
        
        return message;
    }
}