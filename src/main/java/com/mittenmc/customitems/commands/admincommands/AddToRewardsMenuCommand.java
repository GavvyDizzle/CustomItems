package com.mittenmc.customitems.commands.admincommands;

import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.SubCommand;
import com.mittenmc.customitems.CustomItems;
import com.mittenmc.customitems.items.CustomItemStack;
import com.mittenmc.customitems.items.ItemManager;
import me.gavvydizzle.rewardsinventory.api.RewardsInventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

// Requires RewardsInventory active to be loaded!
public class AddToRewardsMenuCommand extends SubCommand {

    private final RewardsInventoryAPI rewardsInventoryAPI;
    private final CustomItems instance;
    private final ItemManager itemManager;

    public AddToRewardsMenuCommand(CustomItems instance, ItemManager itemManager) {
        this.instance = instance;
        this.itemManager = itemManager;
        rewardsInventoryAPI = RewardsInventoryAPI.getInstance();
    }

    @Override
    public String getName() {
        return "addItem";
    }

    @Override
    public String getDescription() {
        return "Adds a custom item to the player's /rew pages inventory";
    }

    @Override
    public String getSyntax() {
        return "/customitems addMisc <player> <itemID> <menuID>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[1]);
        if (offlinePlayer == null) {
            offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                sender.sendMessage(ChatColor.RED + args[1] + " is not a valid player.");
                return;
            }
        }

        CustomItemStack customItemStack = itemManager.getCustomItem(args[2]);
        if (customItemStack == null) {
            sender.sendMessage(ChatColor.RED + "No item exists with the id: " + args[2]);
            return;
        }

        int pageMenuID = rewardsInventoryAPI.getMenuID(args[3]);
        if (pageMenuID == -1) {
            sender.sendMessage(ChatColor.RED + "No menu exists with the id: " + args[3]);
            return;
        }

        int amount;
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "'" + args[4] + " is not a valid amount");
                return;
            }

            if (amount <= 0 || amount > 64) {
                sender.sendMessage(ChatColor.RED + "The amount must be between 1 and 64. You requested " + amount);
                return;
            }

            ItemStack itemStack = customItemStack.getItem().clone();
            if (!customItemStack.isUsesItem()) amount = Numbers.constrain(amount, 1, itemStack.getMaxStackSize());
            itemStack.setAmount(amount);
            if (!rewardsInventoryAPI.addItem(offlinePlayer, pageMenuID, itemStack)) {
                sender.sendMessage(ChatColor.RED + "Failed to add the item");
                return;
            }
        }
        else {
            amount = 1;
            if (!rewardsInventoryAPI.addItem(offlinePlayer, pageMenuID, customItemStack.getItem())) {
                sender.sendMessage(ChatColor.RED + "Failed to add the item");
                return;
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Successfully put " + amount + " " + customItemStack.getId() + " into " + offlinePlayer.getName() + "'s /rew " + args[3] + " menu");
        instance.getLogger().info(offlinePlayer.getName() + " received the item: " + customItemStack.getId());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            return null;
        }
        else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], itemManager.getSortedCustomItemStackIDs(), list);
        }
        else if (args.length == 4) {
            StringUtil.copyPartialMatches(args[3], rewardsInventoryAPI.getPageMenuNames(), list);
        }

        return list;
    }
}