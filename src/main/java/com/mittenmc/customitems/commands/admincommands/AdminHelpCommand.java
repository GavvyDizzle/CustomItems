package com.mittenmc.customitems.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import com.mittenmc.customitems.commands.AdminCommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminHelpCommand extends SubCommand {

    private final AdminCommandManager adminCommandManager;

    public AdminHelpCommand(AdminCommandManager adminCommandManager) {
        this.adminCommandManager = adminCommandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Opens this help menu";
    }

    @Override
    public String getSyntax() {
        return "/customitems help";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD +  "-----(CustomItems Admin Commands)-----");
        ArrayList<SubCommand> subCommands = adminCommandManager.getSubcommands();
        for (SubCommand subCommand : subCommands) {
            sender.sendMessage(ChatColor.GOLD + subCommand.getSyntax() + " - " + ChatColor.YELLOW + subCommand.getDescription());
        }
        sender.sendMessage(ChatColor.GOLD +  "-----(CustomItems Admin Commands)-----");
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}