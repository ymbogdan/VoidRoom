package it.ymbogdan.voidroom;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class VoidRoomListener implements Listener {
    private final VoidRoomManager manager;

    public VoidRoomListener(VoidRoomManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (manager.isInVoidRoom(player.getLocation())) {
            manager.handleElytraEquip(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!manager.isInVoidRoom(player.getLocation())) {
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        boolean cursorElytra = cursor != null && cursor.getType() == Material.ELYTRA;
        boolean clickedElytra = clicked != null && clicked.getType() == Material.ELYTRA;
        if (!cursorElytra && !clickedElytra) {
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot == 38) {
            event.setCancelled(true);
            manager.handleElytraEquip(player);
            player.updateInventory();
            return;
        }
        if (clickedElytra && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            PlayerInventory inventory = player.getInventory();
            if (event.getClickedInventory() != null
                    && event.getClickedInventory().equals(inventory)
                    && inventory.getChestplate() == null) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!manager.isInVoidRoom(player.getLocation())) {
            return;
        }
        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() != Material.ELYTRA) {
            return;
        }
        if (event.getRawSlots().contains(38)) {
            event.setCancelled(true);
            player.updateInventory();
            manager.handleElytraEquip(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!manager.isInVoidRoom(player.getLocation())) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ELYTRA) {
            return;
        }
        event.setCancelled(true);
        if (event.getHand() == EquipmentSlot.HAND) {
            player.updateInventory();
        }
        manager.handleElytraEquip(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!manager.isInVoidRoom(player.getLocation())) {
            return;
        }
        if (event.getRecipe() != null && event.getRecipe().getResult().getType() == Material.ELYTRA) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        if (manager.isInVoidRoom(event.getTo())) {
            manager.handleElytraEquip(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if (manager.isInVoidRoom(player.getLocation())) {
            manager.getPlugin().getServer().getScheduler().runTask(manager.getPlugin(), () -> manager.handleElytraEquip(player));
        }
    }
}
