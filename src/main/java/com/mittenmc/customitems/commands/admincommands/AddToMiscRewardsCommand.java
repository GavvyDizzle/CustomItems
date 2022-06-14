package com.mittenmc.customitems.commands.admincommands;

import com.mittenmc.customitems.CustomItems;
import com.mittenmc.customitems.commands.SubCommand;
import com.mittenmc.customitems.items.CustomItemStack;
import me.gavvydizzle.rewardsinventory.api.RewardsInventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AddToMiscRewardsCommand extends SubCommand {

    private final RewardsInventoryAPI rewardsInventoryAPI;

    public AddToMiscRewardsCommand() {
        rewardsInventoryAPI = RewardsInventoryAPI.getInstance();
    }

    @Override
    public String getName() {
        return "addMisc";
    }

    @Override
    public String getDescription() {
        return "Adds the custom item to the player's /rew misc inventory";
    }

    @Override
    public String getSyntax() {
        return "/customitems addMisc <player> <item-id> <amount>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Invalid player");
            return;
        }

        CustomItemStack customItemStack = CustomItems.getInstance().getItemManager().getCustomItem(args[2]);
        if (customItemStack == null) {
            sender.sendMessage(ChatColor.RED + "No item exists with the id: " + args[2]);
            return;
        }

        int amount;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "'" + args[3] + " is not a valid amount");
                return;
            }

            if (amount <= 0 || amount > 64) {
                sender.sendMessage(ChatColor.RED + "The amount must be between 1 and 64. You requested " + amount);
                return;
            }

            ItemStack itemStack = customItemStack.getItem().clone();
            itemStack.setAmount(amount);
            rewardsInventoryAPI.addMiscItem(player, itemStack);
        }
        else {
            amount = 1;
            rewardsInventoryAPI.addMiscItem(player, customItemStack.getItem());
        }
        sender.sendMessage(ChatColor.GREEN + "Successfully put " + amount + " " + customItemStack.getId() + " into " + player.getName() + "'s /rew misc inventory");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2) {
            return null;
        }
        else if (args.length == 3) {
            return CustomItems.getInstance().getItemManager().getSortedCustomItemStackIDs();
        }

        return new ArrayList<>();
    }
}