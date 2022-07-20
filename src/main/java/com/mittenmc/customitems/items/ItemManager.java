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
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class ItemManager implements Listener {

    private final Map<String, CustomItemStack> customItemStacks;
    private ArrayList<CustomItemStack> sortedCustomItemStacks;
    private ArrayList<String> sortedCustomItemStackIDs;

    private boolean isStoppingCustomItemPlacement;
    private String stopPlacementMessage;

    public ItemManager() {
        customItemStacks = new HashMap<>();
        sortedCustomItemStacks = new ArrayList<>();
        reload();
    }

    public void reload() {
        FileConfiguration config = CustomItems.getInstance().getConfig();
        config.options().copyDefaults(true);
        config.addDefault("stopCustomItemPlacement", true);
        config.addDefault("stopPlacementMessage", "&cYou cannot place this");
        CustomItems.getInstance().saveConfig();

        isStoppingCustomItemPlacement = config.getBoolean("stopCustomItemPlacement");
        stopPlacementMessage = Colors.conv(config.getString("stopPlacementMessage"));

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

                config.options().copyDefaults(true);
                config.addDefault("items", new HashMap<>());
                config.saveToString();

                if (config.getConfigurationSection("items") == null) {
                    Bukkit.getLogger().warning("The file " + config.getName() + " is empty");
                }
                else {
                    for (String key : config.getConfigurationSection("items").getKeys(false)) {
                        String path = "items." + key;

                        String id = key.toLowerCase();
                        if (customItemStacks.containsKey(id)) {
                            Bukkit.getLogger().warning("You have defined '" + key + "' multiple times. This occurrence is in " + config.getName());
                            continue;
                        }

                        try {
                            customItemStacks.put(key.toLowerCase(), new CustomItemStack(
                                    key.toLowerCase(),
                                    config.getString(path + ".displayName"),
                                    config.getString(path + ".material"),
                                    config.getStringList(path + ".lore"),
                                    config.getBoolean(path + ".isEnchanted"),
                                    config.getBoolean(path + ".isSkull"),
                                    config.getString(path + ".skullLink")
                            ));
                        }
                        catch (Exception e) {
                            Bukkit.getLogger().warning("Failed to add item " + key + " from file " + config.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void onCustomItemPlace(BlockPlaceEvent e) {
        if (isStoppingCustomItemPlacement && e.getItemInHand().getItemMeta() != null && e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(CustomItems.getInstance(), "custom_item_id"), PersistentDataType.STRING)) {
            e.setCancelled(true);
            if (!stopPlacementMessage.trim().isEmpty()) {
                e.getPlayer().sendMessage(stopPlacementMessage);
            }
        }
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

}