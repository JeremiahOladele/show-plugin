package net.storryteller.showPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class ShowPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Plugin Disabled");
    }
}
