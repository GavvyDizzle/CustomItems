package com.mittenmc.customitems.commands.admincommands;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.mittenmc.customitems.CustomItems;
import com.mittenmc.customitems.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaveItemCommand extends SubCommand {

    @Override
    public String getName() {
        return "saveItem";
    }

    @Override
    public String getDescription() {
        return "Saves the item in your hand a file";
    }

    @Override
    public String getSyntax() {
        return "/customitems saveItem <item-id> <fileName>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You are not holding a valid item");
            return;
        }

        String itemID = args[1];

        if (args.length == 2) {
            if (saveItem(new File(CustomItems.getInstance().getDataFolder(), "config.yml"), itemID, itemStack)) {
                sender.sendMessage(ChatColor.GREEN + "Successfully saved you held item to config.yml under items." + itemID);
            }
            else {
                sender.sendMessage(ChatColor.RED + "Failed to save your item");
            }
        }
        else {
            String filePath = args[2];
            File file;
            try {
                file = (File) findFile(filePath, CustomItems.getInstance().getDataFolder().getPath()).toArray()[0];
            }
            catch (Exception ignored) {
                sender.sendMessage(ChatColor.YELLOW + "No file exists with the name " + args[2] + ". One will be created");

                String name = args[2].endsWith(".yml") ? args[2] : args[2] + ".yml";
                file = new File(CustomItems.getInstance().getDataFolder(), name);
            }

            if (saveItem(file, itemID, itemStack)) {
                sender.sendMessage(ChatColor.GREEN + "Successfully saved you held item to " + file.getPath() + " under items." + itemID);
            }
            else {
                sender.sendMessage(ChatColor.RED + "Failed to save your item");
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 3) {
            return getFileNames();
        }
        return new ArrayList<>();
    }

    private ArrayList<String> getFileNames() {
        ArrayList<String> fileNames = new ArrayList<>();

        for (final File fileEntry : Objects.requireNonNull(CustomItems.getInstance().getDataFolder().listFiles())) {
            if (fileEntry.isDirectory()) continue;

            if (!fileEntry.getName().endsWith(".yml")) continue;

            fileNames.add(fileEntry.getName());
        }
        return fileNames;
    }

    private Collection<Path> findFile(String fileName, String searchDirectory) throws IOException {
        try (Stream<Path> files = Files.walk(Paths.get(searchDirectory))) {
            return files
                    .filter(f -> f.getFileName().toString().equals(fileName))
                    .collect(Collectors.toList());
        }
    }

    private boolean saveItem(File file, String itemID, ItemStack itemStack) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String path = "items." + itemID;
        if (config.contains(path)) return false;

        XMaterial xMaterial = XMaterial.matchXMaterial(itemStack);
        if (xMaterial.parseMaterial() == Material.PLAYER_HEAD) {
            assert itemStack.getItemMeta() != null;
            config.set(path + ".displayName", itemStack.getItemMeta().getDisplayName());
            config.set(path + ".lore", itemStack.getItemMeta().getLore());
            config.set(path + ".isSkull", true);
            config.set(path + ".skullLink", SkullUtils.getSkinValue(itemStack.getItemMeta()));
        }
        else {
            assert itemStack.getItemMeta() != null;
            config.set(path + ".displayName", itemStack.getItemMeta().getDisplayName());
            assert xMaterial.parseMaterial() != null;
            config.set(path + ".material", xMaterial.parseMaterial().toString());
            config.set(path + ".lore", itemStack.getItemMeta().getLore());
            config.set(path + ".isEnchanted", itemStack.getItemMeta().hasEnchants());
            config.set(path + ".isSkull", false);
        }

        try {
            config.save(file);
        }
        catch (IOException e) {
            System.out.println("Could not save file: " + config.getName());
        }
        return true;
    }


}