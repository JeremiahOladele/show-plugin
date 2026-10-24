package net.storryteller.showPlugin.internal.commands;

import net.storryteller.showPlugin.ShowPlugin;
import net.storryteller.showPlugin.internal.AnimatedStructure;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BuildStructureCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        File structureFile = findStructure(args[0]);

        try {
            AnimatedStructure.BuildSession session = ShowPlugin.structureBuilder.buildAnimatedStructure(structureFile, player.getLocation());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    private File findStructure(@NotNull String arg) {
        File dataFile = new File(ShowPlugin.getPluginInstance().getDataFolder(), "structures/" + arg);

        return dataFile;
    }
}
