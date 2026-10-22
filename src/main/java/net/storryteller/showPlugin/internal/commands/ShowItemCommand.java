package net.storryteller.showPlugin.internal.commands;

import net.storryteller.showPlugin.api.item.CustomItem;
import net.storryteller.showPlugin.api.enums.CustomItemEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ShowItemCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        CustomItemEnum type = findItem(args[0]);

        if (type == null) {
            player.sendMessage(ChatColor.RED + "Unknown custom item: " + args[0]);
            return true;
        }

        int amount = 1;
        if (args.length == 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                player.sendMessage(ChatColor.RED + "Amount must be a whole number.");
                return true;
            }
        }

        ItemStack item = CustomItem.get(type, amount);
        player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Gave you " + amount + "x " + type.getName() + ".");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            String prefix = args[0].toUpperCase(Locale.ROOT);

            return Arrays.stream(CustomItemEnum.values())
                    .map(Enum::name)
                    .filter(name -> name.startsWith(prefix))
                    .sorted()
                    .toList();
        }
        return new ArrayList<>();
    }

    private CustomItemEnum findItem(String input) {
        for (CustomItemEnum type : CustomItemEnum.values()) {
            if (type.name().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}
