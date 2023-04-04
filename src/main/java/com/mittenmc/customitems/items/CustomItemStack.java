package com.mittenmc.customitems.items;

import com.cryptomorin.xseries.SkullUtils;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ConfigUtils;
import com.github.mittenmc.serverutils.UUIDConverter;
import com.mittenmc.customitems.CustomItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomItemStack {

    private final String id, uncoloredName;
    private final boolean isPlaceable;
    private final int numUses;
    private final ItemStack item, usesItem, itemListItem, itemListUsesItem;

    public CustomItemStack(String id,
                           boolean isPlaceable,
                           int numUses,
                           String displayName,
                           String material,
                           List<String> lore,
                           boolean isEnchanted,
                           boolean isUsingSkull,
                           String skullString) {

        this.id = id;
        this.isPlaceable = isPlaceable;
        this.numUses = numUses;
        String name = Colors.conv(displayName);
        this.uncoloredName = Colors.strip(name);

        if (isUsingSkull) {
            item = new ItemStack(Material.PLAYER_HEAD);
            assert item.getItemMeta() != null;
            SkullMeta meta = SkullUtils.applySkin(item.getItemMeta(), skullString);
            item.setItemMeta(meta);
        }
        else {
            item = new ItemStack(ConfigUtils.getMaterial(material));
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            if (isEnchanted) {
                meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colors.conv(displayName));
        meta.setLore(Colors.conv(lore));
        meta.getPersistentDataContainer().set(CustomItems.getInstance().getItemManager().getIdKey(), PersistentDataType.STRING, id);
        if (isUsesItem()) meta.getPersistentDataContainer().set(CustomItems.getInstance().getItemManager().getUsagesKey(), PersistentDataType.INTEGER, numUses);
        item.setItemMeta(meta);

        if (isUsesItem()) {
            usesItem = item.clone();
            meta = usesItem.getItemMeta();
            assert meta != null;
            ArrayList<String> usesLore = new ArrayList<>();
            for (String str : Objects.requireNonNull(meta.getLore())) {
                usesLore.add(str.replace("{uses}", String.valueOf(numUses)));
            }
            meta.setLore(usesLore);
            usesItem.setItemMeta(meta);
        }
        else {
            usesItem = null;
        }

        itemListItem = item.clone();
        meta = itemListItem.getItemMeta();
        assert meta != null;
        ArrayList<String> itemListLore = new ArrayList<>(Objects.requireNonNull(meta.getLore()));
        itemListLore.add(ChatColor.DARK_GRAY + "----------------------");
        itemListLore.add(ChatColor.YELLOW + "id: " + ChatColor.GREEN + id);
        meta.setLore(itemListLore);
        itemListItem.setItemMeta(meta);

        if (isUsesItem()) {
            itemListUsesItem = itemListItem.clone();
            meta = itemListUsesItem.getItemMeta();
            assert meta != null;
            ArrayList<String> usesLore = new ArrayList<>();
            for (String str : Objects.requireNonNull(meta.getLore())) {
                usesLore.add(str.replace("{uses}", String.valueOf(numUses)));
            }
            meta.setLore(usesLore);
            itemListUsesItem.setItemMeta(meta);
        }
        else {
            itemListUsesItem = null;
        }
    }

    public boolean isUsesItem() {
        return numUses >= 1;
    }


    public String getId() {
        return id;
    }

    public boolean isPlaceable() {
        return isPlaceable;
    }

    public String getUncoloredName() {
        return uncoloredName;
    }

    public ItemStack getItem() {
        // Add a unique tag to any "uses" item, so they cannot stack
        if (isUsesItem()) {
            ItemStack clone = usesItem.clone();
            ItemMeta meta = clone.getItemMeta();
            assert meta != null;
            meta.getPersistentDataContainer().set(CustomItems.getInstance().getItemManager().getRandKey(), PersistentDataType.BYTE_ARRAY, UUIDConverter.convert(UUID.randomUUID()));
            clone.setItemMeta(meta);
            return clone;
        }
        return item;
    }

    public List<String> getNonUsesLore() {
        return Objects.requireNonNull(item.getItemMeta()).getLore();
    }

    public ItemStack getItemListItem() {
        if (isUsesItem()) return itemListUsesItem;
        return itemListItem;
    }
}
