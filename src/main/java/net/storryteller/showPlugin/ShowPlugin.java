package net.storryteller.showPlugin;

import net.storryteller.showPlugin.api.block.CustomBlockListener;
import net.storryteller.showPlugin.api.block.CustomBlockManager;
import net.storryteller.showPlugin.api.item.CustomItem;
import net.storryteller.showPlugin.api.item.lore.LoreUpdateListener;
import net.storryteller.showPlugin.internal.AnimatedStructure;
import net.storryteller.showPlugin.internal.commands.BuildStructureCommand;
import net.storryteller.showPlugin.internal.commands.ShowItemCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class ShowPlugin extends JavaPlugin {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static ShowPlugin instance;
    public static AnimatedStructure structureBuilder;

    @Override
    public void onEnable() {
        instance = this;

        this.saveResource("config.yml", false);

        System.out.println(ANSI_RESET + ANSI_GREEN +
                "\n " +
                "----------------------------------------- \n " +
                "ShowPlugin has been Enabled Successfully.\n " +
                "-----------------------------------------" + ANSI_RESET);

        CustomItem.initalize(this);
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        System.out.println(ANSI_RESET + ANSI_RED +
                "\n " +
                "------------------------------------------ \n " +
                "ShowPlugin has been Disabled Successfully.\n " +
                "------------------------------------------" + ANSI_RESET);

        instance = null;
    }

    public static JavaPlugin getPluginInstance() {
        return instance;
    }

    private void registerListeners() {
        CustomBlockManager customBlockManager = new CustomBlockManager(this);
        getServer().getPluginManager().registerEvents(new CustomBlockListener(customBlockManager), this);
        getServer().getPluginManager().registerEvents(new LoreUpdateListener(), this);
        structureBuilder = new AnimatedStructure(this);
    }

    private void registerCommands() {
        registerCommand(new ShowItemCommand(), new ShowItemCommand(), "showItem");
        registerCommand(new BuildStructureCommand(), new BuildStructureCommand(), "buildStructure");
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
