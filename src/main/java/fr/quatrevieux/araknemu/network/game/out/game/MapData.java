package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * Send map data
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L407
 */
final public class MapData {
    final private ExplorationMap map;

    public MapData(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "GDM|" + map.id() + "|" + map.date() + "|" + map.key();
    }
}
