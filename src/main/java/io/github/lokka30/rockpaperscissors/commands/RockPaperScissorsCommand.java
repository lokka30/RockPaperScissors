package io.github.lokka30.rockpaperscissors.commands;

import io.github.lokka30.rockpaperscissors.RockPaperScissors;
import io.github.lokka30.rockpaperscissors.utils.RPSAction;
import io.github.lokka30.rockpaperscissors.utils.RPSOutcome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class RockPaperScissorsCommand implements CommandExecutor {

    private RockPaperScissors instance;

    public RockPaperScissorsCommand(RockPaperScissors instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String label, @NotNull final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (player.hasPermission("rockpaperscissors.use")) {
                if (args.length == 0) {
                    for (String msg : instance.fileCache.MESSAGES_INFO) {
                        player.sendMessage(instance.utils.prefix(msg.replaceAll("%version%", instance.getDescription().getVersion())));
                    }
                    instance.fileCache.SETTINGS_SOUND_CMD_SUCCESS.playToPlayer(player);
                } else if (args.length == 1) {
                    switch (args[0].toLowerCase()) {
                        case "rock":
                            startRPS(player, RPSAction.ROCK);
                            break;
                        case "paper":
                            startRPS(player, RPSAction.PAPER);
                            break;
                        case "scissors":
                            startRPS(player, RPSAction.SCISSORS);
                            break;
                        case "random":
                            startRPS(player, RPSAction.RANDOM);
                            break;
                        case "points":
                            player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_POINTS.replaceAll("%points%", Integer.toString(instance.fileCache.getPoints(player)))));
                            instance.fileCache.SETTINGS_SOUND_CMD_SUCCESS.playToPlayer(player);
                            break;
                        case "reload":
                            if (player.hasPermission("rockpaperscissors.reload")) {
                                player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_RELOAD_STARTED));
                                instance.fileCache.cache();
                                player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_RELOAD_COMPLETE));
                                instance.fileCache.SETTINGS_SOUND_CMD_SUCCESS.playToPlayer(player);
                            } else {
                                player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_NO_PERMISSION));
                                instance.fileCache.SETTINGS_SOUND_CMD_FAILURE.playToPlayer(player);
                            }
                            break;
                        default:
                            player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_USAGE));
                            instance.fileCache.SETTINGS_SOUND_CMD_FAILURE.playToPlayer(player);
                            break;
                    }
                } else {
                    player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_USAGE));
                    instance.fileCache.SETTINGS_SOUND_CMD_FAILURE.playToPlayer(player);
                }
            } else {
                player.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_NO_PERMISSION));
                instance.fileCache.SETTINGS_SOUND_CMD_FAILURE.playToPlayer(player);
            }
        } else {
            sender.sendMessage(instance.utils.prefix(instance.fileCache.MESSAGES_MUST_BE_PLAYER));
        }
        return true;
    }

    private RPSAction generateRandomRPSAction() {
        final int index = ThreadLocalRandom.current().nextInt(1, 3 + 1);
        switch (index) {
            case 1:
                return RPSAction.ROCK;
            case 2:
                return RPSAction.PAPER;
            case 3:
                return RPSAction.SCISSORS;
            default:
                throw new IndexOutOfBoundsException("Index value of " + index + " is undefined");
        }
    }

    private void startRPS(Player player, RPSAction playerAction) {
        instance.fileCache.SETTINGS_SOUND_GAME_START.playToPlayer(player);

        if (playerAction == RPSAction.RANDOM) {
            playerAction = generateRandomRPSAction();
        }

        switch (playerAction) {
            case ROCK:
                instance.fileCache.SETTINGS_TITLE_START_ROCK.send(player);
                break;
            case PAPER:
                instance.fileCache.SETTINGS_TITLE_START_PAPER.send(player);
                break;
            case SCISSORS:
                instance.fileCache.SETTINGS_TITLE_START_SCISSORS.send(player);
                break;
            default:
                throw new IllegalStateException("Undefined RPSAction: " + playerAction.toString());
        }

        //The runnable is made for the 'outcome delay'.
        RPSAction finalPlayerAction = playerAction;
        new BukkitRunnable() {
            @Override
            public void run() {
                RPSAction botAction = generateRandomRPSAction();

                switch (finalPlayerAction) {
                    case ROCK:

                        switch (botAction) {
                            case ROCK:
                                rpsFinished(player, RPSOutcome.DRAW);
                                break;
                            case PAPER:
                                rpsFinished(player, RPSOutcome.LOSS);
                                break;
                            case SCISSORS:
                                rpsFinished(player, RPSOutcome.WIN);
                                break;
                            default:
                                return;
                        }

                        break;
                    case PAPER:

                        switch (botAction) {
                            case ROCK:
                                rpsFinished(player, RPSOutcome.WIN);
                                break;
                            case PAPER:
                                rpsFinished(player, RPSOutcome.DRAW);
                                break;
                            case SCISSORS:
                                rpsFinished(player, RPSOutcome.LOSS);
                                break;
                            default:
                                return;
                        }

                        break;
                    case SCISSORS:

                        switch (botAction) {
                            case ROCK:
                                rpsFinished(player, RPSOutcome.LOSS);
                                break;
                            case PAPER:
                                rpsFinished(player, RPSOutcome.WIN);
                                break;
                            case SCISSORS:
                                rpsFinished(player, RPSOutcome.DRAW);
                                break;
                            default:
                                return;
                        }

                        break;
                    default:
                        //Illegal state exception already thrown. Just return.
                }
            }
        }.runTaskLater(instance, 20L * instance.fileCache.SETTINGS_OUTCOME_DELAY);

    }

    private void rpsFinished(Player player, RPSOutcome outcome) {
        instance.fileCache.modifyPoints(player, outcome);

        switch (outcome) {
            case WIN:
                instance.fileCache.SETTINGS_TITLE_END_WIN.send(player);
                instance.fileCache.SETTINGS_SOUND_GAME_WIN.playToPlayer(player);
                break;
            case DRAW:
                instance.fileCache.SETTINGS_TITLE_END_DRAW.send(player);
                instance.fileCache.SETTINGS_SOUND_GAME_DRAW.playToPlayer(player);
                break;
            case LOSS:
                instance.fileCache.SETTINGS_TITLE_END_LOSS.send(player);
                instance.fileCache.SETTINGS_SOUND_GAME_LOSS.playToPlayer(player);
                break;
            default:
                throw new IllegalStateException("Undefined RPSOutcome state: " + outcome.toString());
        }
    }
}
