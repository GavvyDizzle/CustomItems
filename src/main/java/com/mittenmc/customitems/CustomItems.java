package com.mittenmc.customitems;

import com.mittenmc.customitems.commands.AdminCommandManager;
import com.mittenmc.customitems.gui.GUIManager;
import com.mittenmc.customitems.items.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class CustomItems extends JavaPlugin {

    private static CustomItems instance;
    private ItemManager itemManager;
    private GUIManager guiManager;
    private AdminCommandManager adminCommandManager;
    private boolean isRewardsInventoryLoaded;

    @Override
    public void onEnable() {
        instance = this;

        itemManager = new ItemManager(); // Must create this before the AdminCommandManager
        getServer().getPluginManager().registerEvents(itemManager, this);
        guiManager = new GUIManager();

        getServer().getPluginManager().registerEvents(guiManager, this);

        adminCommandManager = new AdminCommandManager();
        getCommand("customItems").setExecutor(adminCommandManager);

        getConfig().options().copyDefaults(true);
        getConfig().addDefault("items", new HashMap<>());
        saveConfig();

        isRewardsInventoryLoaded = getServer().getPluginManager().getPlugin("RewardsInventory") != null;
    }

    public static CustomItems getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public AdminCommandManager getAdminCommandManager() {
        return adminCommandManager;
    }

    public boolean isRewardsInventoryLoaded() {
        return isRewardsInventoryLoaded;
    }

}
