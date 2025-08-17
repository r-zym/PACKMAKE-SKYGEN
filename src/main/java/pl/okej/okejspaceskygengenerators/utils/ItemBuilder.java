package pl.okej.okejspaceskygengenerators.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    
    private ItemStack itemStack;
    
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }
    
    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }
    
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }
    
    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.colorize(name));
            itemStack.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> colorizedLore = new ArrayList<>();
            for (String line : lore) {
                colorizedLore.add(ColorUtil.colorize(line));
            }
            meta.setLore(colorizedLore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemBuilder addLoreLine(String line) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(ColorUtil.colorize(line));
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    
    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flag);
            itemStack.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemBuilder setCustomModelData(int data) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(data);
            itemStack.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            itemStack.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemStack build() {
        return itemStack.clone();
    }
}