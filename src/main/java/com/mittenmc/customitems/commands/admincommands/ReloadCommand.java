package com.mittenmc.customitems.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import com.mittenmc.customitems.CustomItems;
import com.mittenmc.customitems.items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends SubCommand {

    private final CustomItems instance;
    private final ItemManager itemManager;

    public ReloadCommand(CustomItems instance, ItemManager itemManager) {
        this.instance = instance;
        this.itemManager = itemManager;
    }

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
            instance.reloadConfig();
            itemManager.reload();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "[CustomItems] Encountered an error when reloading. Check the console");
            e.printStackTrace();
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "[CustomItems] Reloaded");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}