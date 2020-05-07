package io.github.lokka30.rockpaperscissors.utils;

import io.github.lokka30.rockpaperscissors.RockPaperScissors;
import io.github.lokka30.rockpaperscissors.enums.LogLevel;
import io.github.lokka30.rockpaperscissors.enums.RPSOutcome;
import io.github.lokka30.rockpaperscissors.objects.CachedSound;
import io.github.lokka30.rockpaperscissors.objects.CachedTitle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileCache {

    public Boolean SETTINGS_CHECK_FOR_UPDATES;
    public String MESSAGES_PREFIX;
    public List<String> MESSAGES_INFO;
    public CachedSound SETTINGS_SOUND_CMD_SUCCESS;
    public CachedSound SETTINGS_SOUND_CMD_FAILURE;
    public CachedSound SETTINGS_SOUND_GAME_WIN;
    public CachedSound SETTINGS_SOUND_GAME_DRAW;
    public CachedSound SETTINGS_SOUND_GAME_LOSS;
    public String MESSAGES_NO_PERMISSION;
    public String MESSAGES_RELOAD_STARTED;
    public String MESSAGES_RELOAD_COMPLETE;
    public String MESSAGES_USAGE;
    public String MESSAGES_MUST_BE_PLAYER;
    public CachedTitle SETTINGS_TITLE_START_ROCK;
    public CachedTitle SETTINGS_TITLE_START_PAPER;
    public CachedTitle SETTINGS_TITLE_START_SCISSORS;
    public CachedTitle SETTINGS_TITLE_END_WIN;
    public CachedTitle SETTINGS_TITLE_END_DRAW;
    public CachedTitle SETTINGS_TITLE_END_LOSS;
    public int SETTINGS_OUTCOME_DELAY;
    public String MESSAGES_POINTS;
    public HashMap<Player, Integer> pointsMap;
    public CachedSound SETTINGS_SOUND_GAME_START;
    private RockPaperScissors instance;

    public FileCache(RockPaperScissors instance) {
        this.instance = instance;
    }

    public void cache() {
        instance.utils.log(LogLevel.INFO, "Caching file values...");

        SETTINGS_CHECK_FOR_UPDATES = instance.settings.get("check-for-updates", true);
        MESSAGES_PREFIX = instance.messages.get("prefix", "[RPS]");
        MESSAGES_INFO = instance.messages.get("info", Collections.singletonList("This server is running RockPaperScissors by lokka30."));
        SETTINGS_SOUND_CMD_SUCCESS = loadCachedSound("cmd-success");
        SETTINGS_SOUND_CMD_FAILURE = loadCachedSound("cmd-failure");
        SETTINGS_SOUND_GAME_WIN = loadCachedSound("game-win");
        SETTINGS_SOUND_GAME_DRAW = loadCachedSound("game-draw");
        SETTINGS_SOUND_GAME_LOSS = loadCachedSound("game-loss");
        MESSAGES_NO_PERMISSION = instance.messages.get("no-permission", "no permission");
        MESSAGES_RELOAD_STARTED = instance.messages.get("reload-started", "reload started");
        MESSAGES_RELOAD_COMPLETE = instance.messages.get("reload-complete", "reload complete");
        MESSAGES_USAGE = instance.messages.get("usage", "usage: /rps [...]");
        MESSAGES_MUST_BE_PLAYER = instance.messages.get("must-be-player", "must be player to execute this");
        SETTINGS_TITLE_START_ROCK = loadCachedTitle("start.rock");
        SETTINGS_TITLE_START_PAPER = loadCachedTitle("start.paper");
        SETTINGS_TITLE_START_SCISSORS = loadCachedTitle("start.scissors");
        SETTINGS_TITLE_END_WIN = loadCachedTitle("end.win");
        SETTINGS_TITLE_END_DRAW = loadCachedTitle("end.draw");
        SETTINGS_TITLE_END_LOSS = loadCachedTitle("end.loss");
        SETTINGS_OUTCOME_DELAY = instance.settings.get("outcome-delay", 1);
        MESSAGES_POINTS = instance.messages.get("points", "your score is %points% points");
        SETTINGS_SOUND_GAME_START = loadCachedSound("game-start");

        if (pointsMap == null) {
            pointsMap = new HashMap<>();
        } else {
            pointsMap.clear();
        }

        instance.utils.log(LogLevel.INFO, "... File values cached.");
    }

    public CachedSound loadCachedSound(String id) {
        final String path = "sounds." + id + ".";
        Sound sound = Sound.valueOf(instance.settings.get(path + "id", null));
        float volume = instance.settings.get(path + "volume", 1.0F);
        float pitch = instance.settings.get(path + "pitch", 1.0F);
        return new CachedSound(sound, volume, pitch);
    }

    public CachedTitle loadCachedTitle(String id) {
        final String path = "titles." + id + ".";
        String mainTitle = instance.utils.colorize(instance.settings.get(path + "mainTitle", null));
        String subTitle = instance.utils.colorize(instance.settings.get(path + "subTitle", null));
        int fadeIn = instance.settings.get(path + "fadeIn", 5);
        int stay = instance.settings.get(path + "stay", 20);
        int fadeOut = instance.settings.get(path + "fadeOut", 5);
        return new CachedTitle(mainTitle, subTitle, fadeIn, stay, fadeOut);
    }

    public int getPoints(Player player) {
        final String uuidStr = player.getUniqueId().toString();

        if (!pointsMap.containsKey(player)) {
            pointsMap.put(player, instance.data.getOrSetDefault("players." + uuidStr + ".points", 0));
        }

        return pointsMap.get(player);
    }

    public void modifyPoints(Player player, RPSOutcome outcome) {
        final String uuidStr = player.getUniqueId().toString();


        switch (outcome) {
            case WIN:
                pointsMap.put(player, getPoints(player) + 1);
                break;
            case DRAW:
                //Do nothing.
                break;
            case LOSS:
                pointsMap.put(player, getPoints(player) - 1);
                break;
            default:
                throw new IllegalStateException("Undefined RPSOutcome state: " + outcome.toString());
        }

        instance.data.set("players." + uuidStr + ".points", pointsMap.get(player));
    }
}
