package io.github.lokka30.rockpaperscissors.utils;

import io.github.lokka30.rockpaperscissors.RockPaperScissors;
import io.github.lokka30.rockpaperscissors.enums.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Utils {

    private RockPaperScissors instance;

    public Utils(RockPaperScissors instance) {
        this.instance = instance;
    }

    public List<String> getSupportedServerVersions() {
        return Arrays.asList("1.15.2", "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14", "1.13.2", "1.13.1", "1.13", "1.12.2", "1.12.1", "1.12", "1.11.2", "1.11.1", "1.11", "1.10.2", "1.10.1", "1.10", "1.9.4", "1.9.3", "1.9.2", "1.9.1", "1.9", "1.8.8", "1.8.7", "1.8.6", "1.8.5", "1.8.4", "1.8.3", "1.8.2", "1.8.1", "1.8");
    }

    public int getLatestSettingsFileVersion() {
        return 1;
    }

    public int getLatestMessagesFileVersion() {
        return 1;
    }

    public int getLatestDataFileVersion() {
        return 1;
    }

    public String colorize(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String prefix(String msg) {
        return colorize(msg.replaceFirst("%prefix%", instance.messages.get("prefix", "[RPS]")));
    }

    public void log(LogLevel logLevel, String msg) {
        Logger logger = Bukkit.getLogger();
        msg = colorize("&7" + msg);
        switch (logLevel) {
            case INFO:
                logger.info(msg);
                break;
            case WARNING:
                logger.warning(msg);
                break;
            case SEVERE:
                logger.severe(msg);
                break;
            default:
                throw new IllegalStateException("Illegal LogLevel state, undefined in Utils.log method: " + logLevel.toString());
        }
    }

    public void registerCommand(String label, CommandExecutor executor) {
        PluginCommand command = instance.getCommand(label);

        if (command == null) {
            log(LogLevel.SEVERE, "Unable to register command '&b" + label + "&7', command undefined");
        } else {
            command.setExecutor(executor);
            log(LogLevel.INFO, "Registered command '&b" + label + "&7'.");
        }
    }
}
