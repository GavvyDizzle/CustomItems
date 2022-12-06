package com.mittenmc.customitems.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import com.mittenmc.customitems.CustomItems;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads all data from the config";
    }

    @Override
    public String getSyntax() {
        return "/customitems reload";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        try {
            CustomItems.getInstance().reloadConfig();
            CustomItems.getInstance().getItemManager().reload();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "[CustomItems] Encountered an error when reloading. Check the console");
            e.printStackTrace();
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "[CustomItems] Reloaded");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }
}