package fr.quatrevieux.araknemu.game.event.exploration.action;

import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Event dispatched when a player start to move
 */
final public class PlayerMoving {
    final private GamePlayer player;
    final private Move action;

    public PlayerMoving(GamePlayer player, Move action) {
        this.player = player;
        this.action = action;
    }

    public GamePlayer player() {
        return player;
    }

    public Move action() {
        return action;
    }
}
