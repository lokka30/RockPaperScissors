package io.github.lokka30.rockpaperscissors.objects;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CachedSound {

    private Sound sound;
    private float volume;
    private float pitch;

    public CachedSound(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void playToPlayer(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
