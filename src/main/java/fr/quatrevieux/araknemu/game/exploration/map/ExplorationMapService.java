package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddExplorationMapListeners;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellActionPerformer;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.MapTriggers;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.Teleport;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Service for handle exploration maps
 */
final public class ExplorationMapService implements PreloadableService {
    final private MapTemplateRepository repository;
    final private MapTriggerRepository triggerRepository;
    final private Map<CellAction, CellActionPerformer> actions = new EnumMap<>(CellAction.class);
    final private ListenerAggregate dispatcher;

    final private ConcurrentMap<Integer, ExplorationMap> maps = new ConcurrentHashMap<>();

    public ExplorationMapService(MapTemplateRepository repository, MapTriggerRepository triggerRepository, ListenerAggregate dispatcher) {
        this.repository = repository;
        this.triggerRepository = triggerRepository;
        this.dispatcher = dispatcher;
    }

    /**
     * Load the exploration map
     */
    public ExplorationMap load(int mapId) throws EntityNotFoundException {
        if (!maps.containsKey(mapId)) {
            MapTemplate template = repository.get(mapId);

            maps.put(
                mapId,
                new ExplorationMap(
                    template,
                    new MapTriggers(triggerRepository.findByMap(template), actions)
                )
            );
        }

        return maps.get(mapId);
    }

    @Override
    public void preload(Logger logger) {
        dispatcher.add(new AddExplorationMapListeners());

        register(new Teleport(this));

        logger.info("Loading maps...");

        long start = System.currentTimeMillis();

        Map<Integer, List<MapTrigger>> triggers = triggerRepository
            .all()
            .stream()
            .collect(Collectors.groupingBy(MapTrigger::map))
        ;

        for (MapTemplate map : repository.all()) {
            List<MapTrigger> mapTriggers = triggers.containsKey(map.id())
                ? triggers.get(map.id())
                : new ArrayList<>()
            ;

            maps.put(
                map.id(),
                new ExplorationMap(
                    map,
                    new MapTriggers(mapTriggers, actions)
                )
            );
        }

        long time = System.currentTimeMillis() - start;

        logger.info(maps.size() + " maps successfully loaded in " + time + "ms");
    }

    private void register(CellActionPerformer action) {
        actions.put(action.action(), action);
    }
}
