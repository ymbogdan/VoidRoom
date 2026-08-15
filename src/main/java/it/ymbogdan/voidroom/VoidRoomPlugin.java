package it.ymbogdan.voidroom;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class VoidRoomPlugin extends JavaPlugin {
    private MessageManager messageManager;
    private VoidRoomManager voidRoomManager;
    private VoidRoomSelectionManager selectionManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messageManager = new MessageManager(this);
        messageManager.load();
        voidRoomManager = new VoidRoomManager(this, messageManager);
        selectionManager = new VoidRoomSelectionManager();
        getServer().getPluginManager().registerEvents(new VoidRoomListener(voidRoomManager), this);
        getServer().getPluginManager().registerEvents(new VoidRoomSelectionListener(selectionManager, messageManager), this);
        VoidRoomCommand command = new VoidRoomCommand(voidRoomManager, selectionManager, messageManager);
        if (getCommand("voidroom") != null) {
            getCommand("voidroom").setExecutor(command);
            getCommand("voidroom").setTabCompleter(command);
        }
        voidRoomManager.startPeriodicCheck();
        int pluginId = 33389;
        new Metrics(this, pluginId);
        if (voidRoomManager.hasConfiguredRegion()) {
            getLogger().info("Loaded region '" + voidRoomManager.getRegionName() + "'.");
        } else {
            getLogger().warning("No region named '" + voidRoomManager.getRegionName() + "' was found. Use /voidroom select and /voidroom save.");
        }
        getLogger().info("VoidRoom has been enabled.");
    }

    @Override
    public void onDisable() {
        if (voidRoomManager != null) {
            voidRoomManager.stopPeriodicCheck();
        }
        getLogger().info("VoidRoom has been disabled.");
    }
}
