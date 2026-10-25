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
import java.nio.file.Files;
import java.nio.file.Path;
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
            AnimatedStructure.BuildSession session = ShowPlugin.structureBuilder.buildAnimatedStructure(structureFile, player.getLocation().add(0.0f, -7.0f, 0.0f));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Path structuresDir = ShowPlugin.getPluginInstance().getDataFolder().toPath().resolve("structures");
        if (!Files.exists(structuresDir)) {return List.of();}

        try (var stream = Files.list(structuresDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan structures directory", e);
        }
    }

    private File findStructure(@NotNull String arg) {
        File dataFile = new File(ShowPlugin.getPluginInstance().getDataFolder(), "structures/" + arg);

        return dataFile;
    }
}
