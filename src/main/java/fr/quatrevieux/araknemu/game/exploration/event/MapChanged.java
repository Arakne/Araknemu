package fr.quatrevieux.araknemu.game.exploration.event;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * Event trigger when a player change map
 */
final public class MapChanged {
    final private ExplorationMap map;

    public MapChanged(ExplorationMap map) {
        this.map = map;
    }

    public ExplorationMap map() {
        return map;
    }
}
