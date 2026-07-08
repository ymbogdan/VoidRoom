package it.ymbogdan.voidroom;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VoidRoomSelectionListener implements Listener {
    private final VoidRoomSelectionManager selectionManager;
    private final MessageManager messageManager;

    public VoidRoomSelectionListener(VoidRoomSelectionManager selectionManager, MessageManager messageManager) {
        this.selectionManager = selectionManager;
        this.messageManager = messageManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSelect(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("voidroom.admin")) {
            return;
        }
        if (!selectionManager.isSelecting(player.getUniqueId())) {
            return;
        }
        ItemStack item = event.getItem();
        if (item != null && item.getType() != Material.AIR) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);
        VoidRoomSelectionManager.Selection selection = selectionManager.get(player.getUniqueId());
        int x = event.getClickedBlock().getX();
        int y = event.getClickedBlock().getY();
        int z = event.getClickedBlock().getZ();
        if (action == Action.LEFT_CLICK_BLOCK) {
            selection.setPos1(event.getClickedBlock().getLocation());
            messageManager.sendPos(player, "commands.pos1-set", x, y, z);
            return;
        }
        selection.setPos2(event.getClickedBlock().getLocation());
        messageManager.sendPos(player, "commands.pos2-set", x, y, z);
    }
}
