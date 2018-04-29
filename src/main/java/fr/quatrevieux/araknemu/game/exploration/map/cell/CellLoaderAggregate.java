package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregate cells loaders
 */
final public class CellLoaderAggregate implements CellLoader {
    final private CellLoader[] loaders;

    public CellLoaderAggregate(CellLoader[] loaders) {
        this.loaders = loaders;
    }

    @Override
    public Collection<ExplorationMapCell> load(ExplorationMap map, List<MapTemplate.Cell> cells) {
        return Arrays.stream(loaders)
            .flatMap(loader -> loader.load(map, cells).stream())
            .collect(Collectors.toList())
        ;
    }
}
