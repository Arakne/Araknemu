package fr.quatrevieux.araknemu.game.event.exploration;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * An exploration player is created
 */
final public class ExplorationPlayerCreated {
    final private ExplorationPlayer player;

    public ExplorationPlayerCreated(ExplorationPlayer player) {
        this.player = player;
    }

    public ExplorationPlayer player() {
        return player;
    }
}
