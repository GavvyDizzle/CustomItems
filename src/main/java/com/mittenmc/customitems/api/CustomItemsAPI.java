package com.mittenmc.customitems.api;

import com.mittenmc.customitems.CustomItems;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

/**
 * General api for CustomItems
 * It is accessible via {@link CustomItemsAPI#getInstance()}.
 * @author GavvyDizzle
 * @version 1.0
 */
public class CustomItemsAPI {

    private static final CustomItemsAPI instance;

    static {
        instance = new CustomItemsAPI();
    }

    /**
     * Accesses the api instance.
     * Might be null if this method is called when {@link CustomItems}'s startup method is still being executed.
     *
     * @return The instance of this api
     * @since 1.0
     */
    @Nullable
    public static CustomItemsAPI getInstance() {
        return instance;
    }

    /**
     * Determines if this item is a custom item created with this plugin.
     * This method checks to see is this item has PDC data associated with this plugin.
     * @param itemStack The ItemStack
     * @return True if this item is a custom item, false if not
     */
    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().has(CustomItems.getInstance().getItemManager().getIdKey(), PersistentDataType.STRING);
    }

    /**
     * Gets this item's custom id as defined with this plugin.
     * This method gets the "custom_item_id" field from this item using PDC.
     * @param itemStack The ItemStack
     * @return The item's id or null if none exists
     */
    @Nullable
    public String getCustomItemID(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return null;
        return itemStack.getItemMeta().getPersistentDataContainer().get(CustomItems.getInstance().getItemManager().getIdKey(), PersistentDataType.STRING);
    }
}
