package fr.quatrevieux.araknemu.game.exploration.event;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Event for create exploration
 */
final public class StartExploration {
    final private ExplorationPlayer player;

    public StartExploration(ExplorationPlayer player) {
        this.player = player;
    }

    public ExplorationPlayer player() {
        return player;
    }
}
