package com.mittenmc.customitems.api;

import com.mittenmc.customitems.CustomItems;
import com.mittenmc.customitems.items.ItemManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

/**
 * General api for CustomItems
 * It is accessible via {@link CustomItemsAPI#getInstance()}.
 * @author GavvyDizzle
 * @version 1.0.0
 */
public class CustomItemsAPI {

    private static CustomItemsAPI instance;
    private static ItemManager itemManager;

    private CustomItemsAPI() {} // Singleton class

    /**
     * Accesses the api instance.
     * Might cause an error if this method is called when {@link CustomItems}'s startup method is still being executed.
     *
     * @return The instance of this api
     * @since 1.0.0
     */
    public static CustomItemsAPI getInstance() {
        if (instance == null) {
            instance = new CustomItemsAPI();
            itemManager = CustomItems.getInstance().getItemManager();
        }
        return instance;
    }

    /**
     * Determines if this item is a custom item created with this plugin.
     * This method checks to see is this item has PDC data associated with this plugin.
     * @param itemStack The ItemStack
     * @return True if this item is a custom item, false if not
     * @since 1.0.0
     */
    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        return itemStack.getItemMeta().getPersistentDataContainer().has(itemManager.getIdKey(), PersistentDataType.STRING);
    }

    /**
     * Gets this item's custom id as defined with this plugin.
     * This method gets the "custom_item_id" field from this item using PDC.
     * @param itemStack The ItemStack
     * @return The item's id or null if none exists
     * @since 1.0.0
     */
    @Nullable
    public String getCustomItemID(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return null;
        return itemStack.getItemMeta().getPersistentDataContainer().get(itemManager.getIdKey(), PersistentDataType.STRING);
    }

    /**
     * Determines if this item has "uses" as defined by this plugin.
     * This method checks to see if this item has a specific PDC entry.
     * @param itemStack The ItemStack
     * @return True if this item has uses, false if not
     * @since 1.0.0
     */
    public boolean doesItemHaveUses(ItemStack itemStack) {
        return itemManager.doesCustomItemHaveUses(itemStack);
    }

    /**
     * Gets this item's uses as defined with this plugin.
     * This method gets the "uses_remaining" field from this item using PDC.
     * @param itemStack The ItemStack
     * @return The item's uses or -1 if it cannot be found
     * @since 1.0.0
     */
    public int getItemUses(ItemStack itemStack) {
        return itemManager.getCustomItemUses(itemStack);
    }

    /**
     * Decreases the uses of this custom item.
     * This method automatically updates the lore so there is no need to manually call {@link CustomItemsAPI#updateUsageLore(ItemStack)}.
     * @param itemStack The ItemStack
     * @param decreaseAmount The amount to decrease by
     * @return If the amount decreased successfully
     * @since 1.0.0
     */
    public boolean decreaseItemUses(ItemStack itemStack, int decreaseAmount) {
        if (!doesItemHaveUses(itemStack)) return false;

        try {
            ItemMeta meta = itemStack.getItemMeta();
            assert meta != null;
            meta.getPersistentDataContainer().set(
                    CustomItems.getInstance().getItemManager().getUsagesKey(),
                    PersistentDataType.INTEGER,
                    itemManager.getCustomItemUses(itemStack) - decreaseAmount
            );
            itemStack.setItemMeta(meta);
            itemManager.updateUsesLore(itemStack);

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Forces a manual reload of the item's lore to update its uses.
     * This method will do nothing if the item does not have uses.
     * @param itemStack The ItemStack
     * @return If the lore updated successfully
     * @since 1.0.0
     */
    public boolean updateUsageLore(ItemStack itemStack) {
        if (!doesItemHaveUses(itemStack)) return false;
        return itemManager.updateUsesLore(itemStack);
    }
}
