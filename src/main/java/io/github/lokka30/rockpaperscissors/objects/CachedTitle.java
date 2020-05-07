package io.github.lokka30.rockpaperscissors.objects;

import org.bukkit.entity.Player;

public class CachedTitle {

    private String mainTitle;
    private String subTitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public CachedTitle(String mainTitle, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public void send(Player player) {
        player.sendTitle(mainTitle, subTitle, fadeIn, stay, fadeOut);
    }
}
