package fr.quatrevieux.araknemu.game.event.exploration.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Move;

/**
 * Event dispatched when a player start to move
 */
final public class PlayerMoving {
    final private ExplorationPlayer player;
    final private Move action;

    public PlayerMoving(ExplorationPlayer player, Move action) {
        this.player = player;
        this.action = action;
    }

    public ExplorationPlayer player() {
        return player;
    }

    public Move action() {
        return action;
    }
}
