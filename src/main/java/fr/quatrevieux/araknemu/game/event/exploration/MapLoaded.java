package fr.quatrevieux.araknemu.game.event.exploration;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * Event dispatched when the map is successfully loaded
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
