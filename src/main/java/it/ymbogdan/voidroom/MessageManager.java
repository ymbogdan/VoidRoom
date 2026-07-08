package it.ymbogdan.voidroom;

import it.ymbogdan.voidroom.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {
    private final JavaPlugin plugin;
    private FileConfiguration messages;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "message.yml");
        if (!file.exists()) {
            plugin.saveResource("message.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path) {
        return get(path, Map.of());
    }

    public String get(String path, Map<String, String> placeholders) {
        String value = messages.getString(path);
        if (value == null) {
            return "";
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return TextUtil.colorize(value);
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, Map.of());
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        String message = get(path, placeholders);
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    public void sendList(CommandSender sender, String path) {
        List<String> lines = messages.getStringList(path);
        for (String line : lines) {
            sender.sendMessage(TextUtil.colorize(line));
        }
    }

    public void sendPos(Player player, String path, int x, int y, int z) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("x", String.valueOf(x));
        placeholders.put("y", String.valueOf(y));
        placeholders.put("z", String.valueOf(z));
        send(player, path, placeholders);
    }
}
