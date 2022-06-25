package com.mittenmc.customitems.items;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.github.mittenmc.serverutils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomItemStack {

    private final String id, uncoloredName;
    private final ItemStack item, itemListItem;

    public CustomItemStack(String id,
                           String displayName,
                           String material,
                           List<String> lore,
                           boolean isEnchanted,
                           boolean isUsingSkull,
                           String skullString) {

        this.id = id;
        String name = Colors.conv(displayName);
        this.uncoloredName = Colors.strip(name);

        ArrayList<String> newLore = new ArrayList<>(lore.size());
        for (String str : lore) {
            newLore.add(Colors.conv(str));
        }

        if (isUsingSkull) {
            item = new ItemStack(Material.PLAYER_HEAD);
            assert item.getItemMeta() != null;
            SkullMeta meta = SkullUtils.applySkin(item.getItemMeta(), skullString);
            meta.setDisplayName(name);
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
        else {
            XMaterial xMaterial = XMaterial.matchXMaterial(material).isPresent() ? XMaterial.matchXMaterial(material).get() : XMaterial.DIRT;
            item = xMaterial.parseItem();
            assert item != null;
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName(name);
            meta.setLore(newLore);

            if (isEnchanted) {
                meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }

        itemListItem = item.clone();
        ItemMeta meta = itemListItem.getItemMeta();
        ArrayList<String> itemListLore = new ArrayList<>(newLore);
        itemListLore.add(ChatColor.DARK_GRAY + "----------------------");
        itemListLore.add(ChatColor.YELLOW + "id: " + ChatColor.GREEN + id);
        assert meta != null;
        meta.setLore(itemListLore);
        itemListItem.setItemMeta(meta);
    }

    public String getId() {
        return id;
    }

    public String getUncoloredName() {
        return uncoloredName;
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getItemListItem() {
        return itemListItem;
    }
}
