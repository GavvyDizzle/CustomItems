package com.mittenmc.customitems.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {

    /**
     * @return The name of the subcommand
     */
    public abstract String getName();

    /**
     * @return A short explanation of what this command does
     */
    public abstract String getDescription();

    /**
     * @return The command usage
     */
    public abstract String getSyntax();

    /**
     * @return The command usage with colors
     */
    public abstract String getColoredSyntax();

    /**
     * Called when an individual subcommand is run
     *
     * @param sender The sender of the command
     * @param args The arguments of the command (/cmd test a b c -> [a, b, c])
     */
    public abstract void perform(CommandSender sender, String[] args);

    /**
     * Gets a list of suggested responses for this subcommand.
     *
     * @param player The player
     * @param args Whole arguments array
     * @return A String list of suggested responses
     */
    public abstract List<String> getSubcommandArguments(Player player, String[] args);

}