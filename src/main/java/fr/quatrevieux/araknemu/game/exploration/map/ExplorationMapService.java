package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle exploration maps
 */
final public class ExplorationMapService implements PreloadableService {
    final private MapTemplateRepository repository;

    final private ConcurrentMap<Integer, ExplorationMap> maps = new ConcurrentHashMap<>();

    public ExplorationMapService(MapTemplateRepository repository) {
        this.repository = repository;
    }

    /**
     * Load the exploration map
     */
    public ExplorationMap load(int mapId) throws EntityNotFoundException {
        if (!maps.containsKey(mapId)) {
            maps.put(
                mapId,
                new ExplorationMap(repository.get(mapId))
            );
        }

        return maps.get(mapId);
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading maps...");

        long start = System.currentTimeMillis();

        for (MapTemplate map : repository.all()) {
            maps.put(map.id(), new ExplorationMap(map));
        }

        long time = System.currentTimeMillis() - start;

        logger.info(maps.size() + " maps successfully loaded in " + time + "ms");
    }
}
