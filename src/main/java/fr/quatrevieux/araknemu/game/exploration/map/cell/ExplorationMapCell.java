package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

/**
 * Cell of exploration map
 */
public interface ExplorationMapCell extends MapCell {
    @Override
    public ExplorationMap map();
}
