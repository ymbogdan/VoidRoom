package it.ymbogdan.voidroom.util;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private TextUtil() {
    }

    public static String colorize(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char character : hex.toCharArray()) {
                replacement.append('§').append(character);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
