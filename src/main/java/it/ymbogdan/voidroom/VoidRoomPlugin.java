package it.ymbogdan.voidroom;

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
    }

    @Override
    public void onDisable() {
        if (voidRoomManager != null) {
            voidRoomManager.stopPeriodicCheck();
        }
    }
}
