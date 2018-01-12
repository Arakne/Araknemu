package fr.quatrevieux.araknemu.game.event.common;

import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * The player is loaded, ready to join the game
 */
final public class PlayerLoaded {
    final private GamePlayer player;

    public PlayerLoaded(GamePlayer player) {
        this.player = player;
    }

    public GamePlayer player() {
        return player;
    }
}
