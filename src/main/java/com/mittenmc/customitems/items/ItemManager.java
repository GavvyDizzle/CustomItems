package com.mittenmc.customitems.items;

import com.mittenmc.customitems.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ItemManager {

    private final Map<String, CustomItemStack> customItemStacks;
    private ArrayList<CustomItemStack> sortedCustomItemStacks;
    private ArrayList<String> sortedCustomItemStackIDs;

    public ItemManager() {
        customItemStacks = new HashMap<>();
        sortedCustomItemStacks = new ArrayList<>();
        reloadAllItems();
    }

    /**
     * Reloads all custom items from all .yml files in the plugin directory
     */
    public void reloadAllItems() {
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
