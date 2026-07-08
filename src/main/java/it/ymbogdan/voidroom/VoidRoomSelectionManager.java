package it.ymbogdan.voidroom;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoidRoomSelectionManager {
    private final Map<UUID, Selection> selections;
    private final Map<UUID, Boolean> selecting;
    private final Map<UUID, Boolean> pendingReset;

    public VoidRoomSelectionManager() {
        this.selections = new HashMap<>();
        this.selecting = new HashMap<>();
        this.pendingReset = new HashMap<>();
    }

    public Selection get(UUID playerId) {
        return selections.computeIfAbsent(playerId, ignored -> new Selection());
    }

    public boolean isSelecting(UUID playerId) {
        return selecting.getOrDefault(playerId, false);
    }

    public boolean hasSelection(UUID playerId) {
        Selection selection = selections.get(playerId);
        return selection != null && (selection.getPos1() != null || selection.getPos2() != null);
    }

    public boolean hasPendingReset(UUID playerId) {
        return pendingReset.getOrDefault(playerId, false);
    }

    public void enableSelecting(UUID playerId) {
        selecting.put(playerId, true);
        pendingReset.remove(playerId);
    }

    public void disableSelecting(UUID playerId) {
        selecting.put(playerId, false);
        pendingReset.remove(playerId);
    }

    public void requestReset(UUID playerId) {
        pendingReset.put(playerId, true);
    }

    public void confirmReset(UUID playerId) {
        clearSelection(playerId);
        pendingReset.remove(playerId);
        selecting.put(playerId, true);
    }

    public void clearSelection(UUID playerId) {
        Selection selection = selections.get(playerId);
        if (selection != null) {
            selection.setPos1(null);
            selection.setPos2(null);
        }
    }

    public void resetAfterSave(UUID playerId) {
        clearSelection(playerId);
        disableSelecting(playerId);
    }

    public static final class Selection {
        private Location pos1;
        private Location pos2;

        public Location getPos1() {
            return pos1;
        }

        public void setPos1(Location pos1) {
            this.pos1 = pos1;
        }

        public Location getPos2() {
            return pos2;
        }

        public void setPos2(Location pos2) {
            this.pos2 = pos2;
        }
    }
}
