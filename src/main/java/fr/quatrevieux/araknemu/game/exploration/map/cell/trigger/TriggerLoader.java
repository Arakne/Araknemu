package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Load trigger cells
 */
final public class TriggerLoader implements CellLoader {
    final private MapTriggerService service;

    public TriggerLoader(MapTriggerService service) {
        this.service = service;
    }

    @Override
    public Collection<ExplorationMapCell> load(ExplorationMap map, List<MapTemplate.Cell> cells) {
        return service.forMap(map).stream()
            .map(action -> new TriggerCell(action.cell(), action))
            .collect(Collectors.toList())
        ;
    }
}
