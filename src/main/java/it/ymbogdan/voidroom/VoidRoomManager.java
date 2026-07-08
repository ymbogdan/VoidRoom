package it.ymbogdan.voidroom;

import it.ymbogdan.voidroom.regions.RegionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoidRoomManager {
    private final JavaPlugin plugin;
    private final MessageManager messageManager;
    private final RegionManager regionManager;
    private final Map<UUID, Long> messageCooldown;
    private BukkitTask periodicTask;
    private String regionName;
    private long messageCooldownMs;
    private long checkIntervalTicks;

    public VoidRoomManager(JavaPlugin plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.regionManager = new RegionManager(plugin);
        this.messageCooldown = new ConcurrentHashMap<>();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        messageManager.load();
        regionName = plugin.getConfig().getString("settings.region-name", "voidroom").toLowerCase();
        messageCooldownMs = plugin.getConfig().getLong("settings.message-cooldown-ms", 2000L);
        checkIntervalTicks = plugin.getConfig().getLong("settings.check-interval-ticks", 5L);
        regionManager.load();
        messageCooldown.clear();
        restartPeriodicCheck();
    }

    public boolean isInVoidRoom(Location location) {
        return regionManager.contains(regionName, location);
    }

    public boolean saveVoidRoom(RegionManager.Region region) {
        boolean saved = regionManager.setRegion(regionName, region);
        if (saved) {
            regionManager.load();
        }
        return saved;
    }

    public void handleElytraEquip(Player player) {
        if (!isInVoidRoom(player.getLocation())) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack chestplate = inventory.getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            removeElytra(player, chestplate);
        }
    }

    public void startPeriodicCheck() {
        stopPeriodicCheck();
        periodicTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (isInVoidRoom(player.getLocation())) {
                    ItemStack chestplate = player.getInventory().getChestplate();
                    if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
                        removeElytra(player, chestplate);
                    }
                }
            }
        }, 0L, checkIntervalTicks);
    }

    public void restartPeriodicCheck() {
        if (periodicTask != null) {
            startPeriodicCheck();
        }
    }

    public void stopPeriodicCheck() {
        if (periodicTask != null) {
            periodicTask.cancel();
            periodicTask = null;
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private void removeElytra(Player player, ItemStack elytra) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            PlayerInventory inventory = player.getInventory();
            ItemStack currentChest = inventory.getChestplate();
            if (currentChest == null || currentChest.getType() != Material.ELYTRA) {
                return;
            }
            inventory.setChestplate(null);
            ItemStack toAdd = elytra.clone();
            toAdd.setAmount(1);
            Map<Integer, ItemStack> overflow = inventory.addItem(toAdd);
            if (!overflow.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), toAdd);
            }
            UUID playerId = player.getUniqueId();
            long now = System.currentTimeMillis();
            Long last = messageCooldown.get(playerId);
            if (last == null || now - last > messageCooldownMs) {
                messageManager.send(player, "elytra-deny");
                messageCooldown.put(playerId, now);
            }
            player.updateInventory();
        });
    }
}
