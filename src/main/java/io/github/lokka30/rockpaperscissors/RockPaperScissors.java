package io.github.lokka30.rockpaperscissors;

import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.FlatFile;
import io.github.lokka30.rockpaperscissors.commands.RockPaperScissorsCommand;
import io.github.lokka30.rockpaperscissors.enums.LogLevel;
import io.github.lokka30.rockpaperscissors.utils.FileCache;
import io.github.lokka30.rockpaperscissors.utils.UpdateChecker;
import io.github.lokka30.rockpaperscissors.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RockPaperScissors extends JavaPlugin {

    public Utils utils;
    public FileCache fileCache;
    public FlatFile settings;
    public FlatFile messages;
    public FlatFile data;

    @Override
    public void onLoad() {
        utils = new Utils(this);
        fileCache = new FileCache(this);
    }

    @Override
    public void onEnable() {
        utils.log(LogLevel.INFO, "&8+---+ &f(Enable Started) &8+---+");
        final long timeStart = System.currentTimeMillis();

        checkCompatibility();
        loadFiles();
        registerCommands();
        registerMetrics();

        final long timeTaken = System.currentTimeMillis() - timeStart;
        utils.log(LogLevel.INFO, "&8+---+ &f(Enable Complete, took &b" + timeTaken + "ms&f) &8+---+");

        checkForUpdates();
    }

    private void checkCompatibility() {
        utils.log(LogLevel.INFO, "&8(&31&8/&34&8) &7Checking compatibility with your server...");

        //Check server version.
        final String currentServerVersion = getServer().getVersion();
        boolean isSupported = false;
        for (String supportedVersion : utils.getSupportedServerVersions()) {
            if (supportedVersion.equals(currentServerVersion)) {
                isSupported = true;
                break;
            }
        }

        if (isSupported) {
            utils.log(LogLevel.INFO, "Detected server version as '&b" + currentServerVersion + "&7' (supported).");
        } else {
            utils.log(LogLevel.INFO, "Detected server version as '&b" + currentServerVersion + "&7'. Your current version of the RockPaperScissors does not support your server's version, you will not receive support for any issues you encounter.");
        }

        //Any other compatibility checks go below.
    }

    private void loadFiles() {
        utils.log(LogLevel.INFO, "&8(&32&8/&34&8) &7Loading files...");

        //Load files.
        settings = LightningBuilder
                .fromFile(new File(getDataFolder() + File.separator + "settings"))
                .addInputStreamFromResource("settings.yml")
                .createYaml();
        messages = LightningBuilder
                .fromFile(new File(getDataFolder() + File.separator + "messages"))
                .addInputStreamFromResource("messages.yml")
                .createYaml();
        data = LightningBuilder
                .fromFile(new File(getDataFolder() + File.separator + "data"))
                .addInputStreamFromResource("data.json")
                .createJson();

        //Check if they exist
        final File settingsFile = new File(getDataFolder() + File.separator + "settings.yml");
        final File messagesFile = new File(getDataFolder() + File.separator + "messages.yml");
        final File dataFile = new File(getDataFolder() + File.separator + "data.json");

        if (!(settingsFile.exists() && !settingsFile.isDirectory())) {
            utils.log(LogLevel.INFO, "File '&bsettings.yml&7' doesn't exist. Creating it now.");
            saveResource("settings.yml", false);
        }

        if (!(messagesFile.exists() && !messagesFile.isDirectory())) {
            utils.log(LogLevel.INFO, "File '&bmessages.yml&7' doesn't exist. Creating it now.");
            saveResource("messages.yml", false);
        }

        if (!(dataFile.exists() && !dataFile.isDirectory())) {
            utils.log(LogLevel.INFO, "File '&bdata.json&7' doesn't exist. Creating it now.");
            saveResource("data.json", false);
        }

        //Check their versions
        if (settings.get("file-version", 0) != utils.getLatestSettingsFileVersion()) {
            utils.log(LogLevel.SEVERE, "File &bsettings.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (messages.get("file-version", 0) != utils.getLatestMessagesFileVersion()) {
            utils.log(LogLevel.SEVERE, "File &bmessages.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (data.get("file-version", 0) != utils.getLatestDataFileVersion()) {
            utils.log(LogLevel.SEVERE, "File &bdata.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        //Cache values.
        fileCache.cache();
    }

    private void registerCommands() {
        utils.log(LogLevel.INFO, "&8(&33&8/&34&8) &7Registering commands...");
        utils.registerCommand("rockpaperscissors", new RockPaperScissorsCommand(this));
    }

    private void registerMetrics() {
        utils.log(LogLevel.INFO, "&8(&34&8/&34&8) &7Registering metrics...");
        new Metrics(this, 7437);
    }
    //TODO metrics url is https://bstats.org/plugin/bukkit/RockPaperScissors/7437

    private void checkForUpdates() {
        if (fileCache.SETTINGS_CHECK_FOR_UPDATES) {
            utils.log(LogLevel.INFO, "&8(&3Update Checker&8) &7Checking for updates...");
            new UpdateChecker(this, 12345).getVersion(version -> {
                final String currentVersion = getDescription().getVersion();

                if (currentVersion.equals(version)) {
                    utils.log(LogLevel.INFO, "&8(&3Update Checker&8) &7You're running the latest version '&b" + currentVersion + "&7'.");
                } else {
                    utils.log(LogLevel.WARNING, "&8(&3Update Checker&8) &7There's a new update available: '&b" + version + "&7'. You're running '&b" + currentVersion + "&7'.");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        utils.log(LogLevel.INFO, "&8+---+ &f(Disable Started) &8+---+");
        final long startTime = System.currentTimeMillis();

        utils.log(LogLevel.INFO, "&8(&31&8/&31&8) &7Clearing pointsMap..");
        fileCache.pointsMap.clear();

        final long totalTime = System.currentTimeMillis() - startTime;
        utils.log(LogLevel.INFO, "&8+---+ &f(Disable Complete, took &b" + totalTime + "ms&f) &8+---+");
    }
}
