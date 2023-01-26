package com.mittenmc.customitems.items;

import com.github.mittenmc.serverutils.Colors;
import com.mittenmc.customitems.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class ItemManager implements Listener {

    private final NamespacedKey idKey, usagesKey;
    private final Map<String, CustomItemStack> customItemStacks;
    private ArrayList<CustomItemStack> sortedCustomItemStacks;
    private ArrayList<String> sortedCustomItemStackIDs;

    private boolean isStoppingCustomItemCrafting, dropExtraItems;
    private String stopPlacementMessage;

    public ItemManager() {
        idKey = new NamespacedKey(CustomItems.getInstance(), "custom_item_id");
        usagesKey = new NamespacedKey(CustomItems.getInstance(), "uses_remaining");
        customItemStacks = new HashMap<>();
        sortedCustomItemStacks = new ArrayList<>();
        reload();
    }

    public void reload() {
        FileConfiguration config = CustomItems.getInstance().getConfig();
        config.options().copyDefaults(true);
        config.addDefault("stopPlacementMessage", "&cYou cannot place this");
        config.addDefault("stopCustomItemCrafting", true);
        config.addDefault("dropExtraItems", false);
        CustomItems.getInstance().saveConfig();

        stopPlacementMessage = Colors.conv(config.getString("stopPlacementMessage"));
        isStoppingCustomItemCrafting = config.getBoolean("stopCustomItemCrafting");
        dropExtraItems = config.getBoolean("dropExtraItems");

        reloadAllItems();
    }

    /**
     * Reloads all custom items from all .yml files in the plugin directory
     */
    private void reloadAllItems() {
        customItemStacks.clear();
        sortedCustomItemStacks.clear();

        try {
            Bukkit.getScheduler().runTaskAsynchronously(CustomItems.getInstance(), () -> {
                parseFolderForFiles(CustomItems.getInstance().getDataFolder());
                sortedCustomItemStacks = new ArrayList<>(customItemStacks.values());
                sortedCustomItemStacks.sort(new ItemSorter());

                sortedCustomItemStackIDs = new ArrayList<>(sortedCustomItemStacks.size());
                for (CustomItemStack customItemStack : sortedCustomItemStacks) {
                    sortedCustomItemStackIDs.add(customItemStack.getId());
                }

                CustomItems.getInstance().getGUIManager().reloadAllGUIs();
            });
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("Failed to load Custom Items");
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    private void parseFolderForFiles(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                parseFolderForFiles(fileEntry);
            } else {
                if (!fileEntry.getName().endsWith(".yml")) continue;

                final FileConfiguration config = YamlConfiguration.loadConfiguration(fileEntry);

                if (config.getConfigurationSection("items") == null) {
                    Bukkit.getLogger().warning("The file " + fileEntry.getName() + " is empty");
                }
                else {
                    for (String key : config.getConfigurationSection("items").getKeys(false)) {
                        String path = "items." + key;

                        String id = key.toLowerCase();
                        if (customItemStacks.containsKey(id)) {
                            Bukkit.getLogger().warning("You have defined '" + key + "' multiple times. This occurrence is in " + fileEntry.getName());
                            continue;
                        }

                        try {
                            customItemStacks.put(key.toLowerCase(), new CustomItemStack(
                                    key.toLowerCase(),
                                    config.getBoolean(path + ".allowPlacement"),
                                    config.getInt(path + ".uses"),
                                    config.getString(path + ".displayName"),
                                    config.getString(path + ".material"),
                                    config.getStringList(path + ".lore"),
                                    config.getBoolean(path + ".isEnchanted"),
                                    config.getBoolean(path + ".isSkull"),
                                    config.getString(path + ".skullLink")
                            ));
                        }
                        catch (Exception e) {
                            Bukkit.getLogger().warning("Failed to add item " + key + " from file " + fileEntry.getName());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onCustomItemPlace(BlockPlaceEvent e) {
        if (isCustomItem(e.getItemInHand())) {
            if (!isCustomItemPlaceable(e.getItemInHand())) {
                e.setCancelled(true);
                if (!stopPlacementMessage.trim().isEmpty()) {
                    e.getPlayer().sendMessage(stopPlacementMessage);
                }
            }
        }
    }

    @EventHandler
    private void onCustomItemCraft(PrepareItemCraftEvent e) {
        if (!isStoppingCustomItemCrafting) return;

        CraftingInventory inv = e.getInventory();
        for (ItemStack item : inv.getStorageContents()) {
            if (isCustomItem(item)) {
                inv.setResult(null);
                return;
            }
        }
    }

    /**
     * Updates the uses in the lore of an item
     * @param itemStack The ItemStack
     * @return If the lore of this item updates
     */
    public boolean updateUsesLore(ItemStack itemStack) {
        CustomItemStack customItemStack = getCustomItem(itemStack);
        if (customItemStack == null || !customItemStack.isUsesItem()) return false;

        try {
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            ArrayList<String> lore = new ArrayList<>();
            for (String str : customItemStack.getNonUsesLore()) {
                lore.add(str.replace("{uses}", "" + getCustomItemUses(itemStack)));
            }
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().has(idKey, PersistentDataType.STRING);
    }

    public boolean isCustomItemPlaceable(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        return getCustomItem(itemStack).isPlaceable();
    }

    public boolean doesCustomItemHaveUses(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().has(usagesKey, PersistentDataType.INTEGER);
    }

    public int getCustomItemUses(ItemStack itemStack) {
        if (!doesCustomItemHaveUses(itemStack)) return -1;
        assert itemStack.getItemMeta() != null;
        return itemStack.getItemMeta().getPersistentDataContainer().get(usagesKey, PersistentDataType.INTEGER);
    }

    /**
     * Gets the CustomItemStack associated with this ItemStack
     * @param itemStack The ItemStack
     * @return The CustomItemStack or null if none exists for this item
     */
    public CustomItemStack getCustomItem(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return null;
        return customItemStacks.get(itemStack.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING));
    }

    /**
     * Gets the CustomItemStack associated with this ID.
     * @param id The ID to search for
     * @return The CustomItemStack, null otherwise
     */
    public CustomItemStack getCustomItem(String id) {
        return customItemStacks.get(id.toLowerCase());
    }

    public ArrayList<CustomItemStack> getSortedCustomItemStacks() {
        return sortedCustomItemStacks;
    }

    public ArrayList<String> getSortedCustomItemStackIDs() {
        return sortedCustomItemStackIDs;
    }

    public boolean isDropExtraItems() {
        return dropExtraItems;
    }

    public NamespacedKey getIdKey() {
        return idKey;
    }

    public NamespacedKey getUsagesKey() {
        return usagesKey;
    }
}
