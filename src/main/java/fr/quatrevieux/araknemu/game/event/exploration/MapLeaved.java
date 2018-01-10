package fr.quatrevieux.araknemu.game.event.exploration;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * Event dispatched when player leave a map
 */
final public class MapLeaved {
    final private ExplorationMap map;

    public MapLeaved(ExplorationMap map) {
        this.map = map;
    }

    public ExplorationMap map() {
        return map;
    }
}
