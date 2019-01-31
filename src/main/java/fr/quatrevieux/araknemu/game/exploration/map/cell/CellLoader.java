package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

import java.util.Collection;
import java.util.List;

/**
 * Load exploration map cells
 */
public interface CellLoader {
    /**
     * Load cells from template cells
     *
     * @param map The exploration map
     * @param cells Cells to load
     */
    public Collection<ExplorationMapCell> load(ExplorationMap map, List<MapTemplate.Cell> cells);
}
