package fr.quatrevieux.araknemu.game.exploration.map.event;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * The exploration map is successfully loaded by the exploration map service
 */
final public class MapLoaded {
    final private ExplorationMap map;

    public MapLoaded(ExplorationMap map) {
        this.map = map;
    }

    public ExplorationMap map() {
        return map;
    }
}
