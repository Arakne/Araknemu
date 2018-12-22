package fr.quatrevieux.araknemu.game.exploration.event;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.map.Direction;

/**
 * Trigger when player change its orientation.
 * Dispatch to map
 */
final public class OrientationChanged {
    final private ExplorationPlayer player;
    final private Direction orientation;

    public OrientationChanged(ExplorationPlayer player, Direction orientation) {
        this.player = player;
        this.orientation = orientation;
    }

    public ExplorationPlayer player() {
        return player;
    }

    public Direction orientation() {
        return orientation;
    }
}
