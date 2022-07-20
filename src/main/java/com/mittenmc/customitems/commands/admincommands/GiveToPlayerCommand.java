package com.mittenmc.customitems.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import com.mittenmc.customitems.CustomItems;
import com.mittenmc.customitems.items.CustomItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GiveToPlayerCommand extends SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives the custom item to the player";
    }

    @Override
    public String getSyntax() {
        return "/customitems give <player> <item-id> <amount>";
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
            player.getInventory().addItem(itemStack);
        }
        else {
            amount = 1;
            player.getInventory().addItem(customItemStack.getItem());
        }
        sender.sendMessage(ChatColor.GREEN + "Successfully gave " + player.getName() + " " + amount + " " + customItemStack.getId());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2) {
            return null;
        }
        else if (args.length == 3) {
            ArrayList<String> list = new ArrayList<>();
            StringUtil.copyPartialMatches(args[2], CustomItems.getInstance().getItemManager().getSortedCustomItemStackIDs(), list);
            return list;
        }

        return new ArrayList<>();
    }
}