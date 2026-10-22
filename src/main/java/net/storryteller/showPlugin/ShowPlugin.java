package net.storryteller.showPlugin;

import net.storryteller.showPlugin.api.item.CustomItem;
import net.storryteller.showPlugin.api.item.lore.LoreUpdateListener;
import net.storryteller.showPlugin.internal.commands.ShowItemCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShowPlugin extends JavaPlugin {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Override
    public void onEnable() {
        System.out.println(ANSI_RESET + ANSI_GREEN +
                "\n " +
                "----------------------------------------- \n " +
                "ShowPlugin has been Enabled Successfully.\n " +
                "-----------------------------------------" + ANSI_RESET);

        getServer().getPluginManager().registerEvents(new LoreUpdateListener(), this);
        CustomItem.initalize(this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        System.out.println(ANSI_RESET + ANSI_RED +
                "\n " +
                "------------------------------------------ \n " +
                "ShowPlugin has been Disabled Successfully.\n " +
                "------------------------------------------" + ANSI_RESET);
    }

    private void registerCommands() {
        registerCommand(new ShowItemCommand(), new ShowItemCommand(), "showItems");
    }

    private void registerCommand(CommandExecutor commandExecutor, TabCompleter tabCompleter, String commandId) {
        PluginCommand command = getCommand(commandId);
        if (command == null) {
            System.out.println(ANSI_RESET + ANSI_YELLOW +
                    "\n " +
                    "Command : '" + commandId + "' does not exist in plugin.yml." + ANSI_RESET);
            return;
        }
        command.setExecutor(commandExecutor);
        command.setTabCompleter(tabCompleter);


    }
}
