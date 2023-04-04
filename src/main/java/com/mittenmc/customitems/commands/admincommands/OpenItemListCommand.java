package com.mittenmc.customitems.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import com.mittenmc.customitems.gui.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class OpenItemListCommand extends SubCommand {

    private final GUIManager guiManager;

    public OpenItemListCommand(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Opens the Item List menu";
    }

    @Override
    public String getSyntax() {
        return "/customitems list";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            guiManager.openItemListInventory((Player) sender);
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}
