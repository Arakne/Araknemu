package fr.quatrevieux.araknemu.game.exploration.event;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * Event dispatched when the player successfully join a map
 */
final public class MapJoined {
    final private ExplorationMap map;

    public MapJoined(ExplorationMap map) {
        this.map = map;
    }

    public ExplorationMap map() {
        return map;
    }
}
