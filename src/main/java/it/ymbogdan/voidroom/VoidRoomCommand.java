package it.ymbogdan.voidroom;

import it.ymbogdan.voidroom.regions.RegionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoidRoomCommand implements CommandExecutor, TabCompleter {
    private final VoidRoomManager manager;
    private final VoidRoomSelectionManager selectionManager;
    private final MessageManager messageManager;

    public VoidRoomCommand(VoidRoomManager manager, VoidRoomSelectionManager selectionManager, MessageManager messageManager) {
        this.manager = manager;
        this.selectionManager = selectionManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messageManager.send(sender, "commands.players-only");
            return true;
        }
        if (!player.hasPermission("voidroom.admin")) {
            messageManager.send(player, "commands.no-permission");
            return true;
        }
        if (args.length == 0) {
            messageManager.sendList(player, "commands.help");
            return true;
        }
        UUID playerId = player.getUniqueId();
        String sub = args[0].toLowerCase();
        if (sub.equals("select")) {
            if (selectionManager.isSelecting(playerId)) {
                selectionManager.disableSelecting(playerId);
                messageManager.send(player, "commands.select-disabled");
                return true;
            }
            if (selectionManager.hasSelection(playerId)) {
                selectionManager.requestReset(playerId);
                messageManager.send(player, "commands.select-reset-warning");
                return true;
            }
            selectionManager.enableSelecting(playerId);
            messageManager.send(player, "commands.select-enabled");
            return true;
        }
        if (sub.equals("confirm")) {
            if (!selectionManager.hasPendingReset(playerId)) {
                messageManager.send(player, "commands.confirm-nothing");
                return true;
            }
            selectionManager.confirmReset(playerId);
            messageManager.send(player, "commands.select-reset-confirmed");
            return true;
        }
        if (sub.equals("pos1")) {
            selectionManager.get(playerId).setPos1(player.getLocation().getBlock().getLocation());
            messageManager.sendPos(player, "commands.pos1-set", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
            return true;
        }
        if (sub.equals("pos2")) {
            selectionManager.get(playerId).setPos2(player.getLocation().getBlock().getLocation());
            messageManager.sendPos(player, "commands.pos2-set", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
            return true;
        }
        if (sub.equals("save")) {
            return save(player);
        }
        if (sub.equals("reload")) {
            manager.reload();
            messageManager.send(player, "commands.reload-success");
            return true;
        }
        messageManager.sendList(player, "commands.help");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = List.of("select", "confirm", "pos1", "pos2", "save", "reload");
            List<String> out = new ArrayList<>();
            String prefix = args[0].toLowerCase();
            for (String option : options) {
                if (option.startsWith(prefix)) {
                    out.add(option);
                }
            }
            return out;
        }
        return List.of();
    }

    private boolean save(Player player) {
        UUID playerId = player.getUniqueId();
        VoidRoomSelectionManager.Selection selection = selectionManager.get(playerId);
        if (selection.getPos1() == null || selection.getPos2() == null) {
            messageManager.send(player, "commands.save-missing-positions");
            return true;
        }
        if (!selection.getPos1().getWorld().getName().equals(selection.getPos2().getWorld().getName())) {
            messageManager.send(player, "commands.save-different-worlds");
            return true;
        }
        RegionManager.Region region = new RegionManager.Region(
                selection.getPos1().getWorld().getName(),
                selection.getPos1().getBlockX(),
                selection.getPos1().getBlockY(),
                selection.getPos1().getBlockZ(),
                selection.getPos2().getBlockX(),
                selection.getPos2().getBlockY(),
                selection.getPos2().getBlockZ()
        );
        if (!manager.saveVoidRoom(region)) {
            messageManager.send(player, "commands.save-error");
            return true;
        }
        selectionManager.resetAfterSave(playerId);
        messageManager.send(player, "commands.save-success");
        return true;
    }
}
