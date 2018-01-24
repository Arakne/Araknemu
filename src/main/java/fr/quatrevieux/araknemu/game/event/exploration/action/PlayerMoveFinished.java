package fr.quatrevieux.araknemu.game.event.exploration.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Event trigger when the player move is terminated
 */
final public class PlayerMoveFinished {
    final private ExplorationPlayer player;

    public PlayerMoveFinished(ExplorationPlayer player) {
        this.player = player;
    }

    public ExplorationPlayer player() {
        return player;
    }
}
