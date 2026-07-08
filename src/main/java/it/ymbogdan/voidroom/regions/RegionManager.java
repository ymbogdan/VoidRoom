package it.ymbogdan.voidroom.regions;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class RegionManager {
    private final JavaPlugin plugin;
    private final Map<String, Region> regions;

    public RegionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
    }

    public void load() {
        regions.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("regions");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            String base = "regions." + key + ".";
            String world = plugin.getConfig().getString(base + "world");
            if (world == null || world.isEmpty()) {
                continue;
            }
            int minX = plugin.getConfig().getInt(base + "minX");
            int minY = plugin.getConfig().getInt(base + "minY");
            int minZ = plugin.getConfig().getInt(base + "minZ");
            int maxX = plugin.getConfig().getInt(base + "maxX");
            int maxY = plugin.getConfig().getInt(base + "maxY");
            int maxZ = plugin.getConfig().getInt(base + "maxZ");
            regions.put(key.toLowerCase(), new Region(world, minX, minY, minZ, maxX, maxY, maxZ));
        }
    }

    public Region getRegion(String name) {
        return regions.get(name.toLowerCase());
    }

    public boolean setRegion(String name, Region region) {
        String normalized = name.toLowerCase();
        regions.put(normalized, region);
        String base = "regions." + normalized + ".";
        plugin.getConfig().set(base + "world", region.world);
        plugin.getConfig().set(base + "minX", region.minX);
        plugin.getConfig().set(base + "minY", region.minY);
        plugin.getConfig().set(base + "minZ", region.minZ);
        plugin.getConfig().set(base + "maxX", region.maxX);
        plugin.getConfig().set(base + "maxY", region.maxY);
        plugin.getConfig().set(base + "maxZ", region.maxZ);
        plugin.saveConfig();
        return true;
    }

    public boolean contains(String regionName, Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        Region region = getRegion(regionName);
        if (region == null) {
            return false;
        }
        return region.contains(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static final class Region {
        private final String world;
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int maxX;
        private final int maxY;
        private final int maxZ;

        public Region(String world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.world = world;
            this.minX = Math.min(minX, maxX);
            this.minY = Math.min(minY, maxY);
            this.minZ = Math.min(minZ, maxZ);
            this.maxX = Math.max(minX, maxX);
            this.maxY = Math.max(minY, maxY);
            this.maxZ = Math.max(minZ, maxZ);
        }

        public boolean contains(String worldName, int x, int y, int z) {
            if (!world.equals(worldName)) {
                return false;
            }
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }
    }
}
