package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.PerformCellActions;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handle exploration map triggers
 */
final public class MapTriggerService implements PreloadableService, EventsSubscriber {
    final private MapTriggerRepository repository;
    final private Map<Integer, Collection<MapTrigger>> triggers = new HashMap<>();
    final private CellActionFactory actionFactory;

    public MapTriggerService(MapTriggerRepository repository, CellActionFactory actionFactory) {
        this.repository = repository;
        this.actionFactory = actionFactory;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading map cells triggers...");

        Collection<MapTrigger> mapTriggers = repository.all();

        for (MapTrigger trigger : mapTriggers) {
            if (!triggers.containsKey(trigger.map())) {
                triggers.put(trigger.map(), new ArrayList<>());
            }

            triggers.get(trigger.map()).add(trigger);
        }

        logger.info("{} triggers loaded", mapTriggers.size());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<MapLoaded>() {
                @Override
                public void on(MapLoaded event) {
                    event.map().dispatcher().add(new PerformCellActions());
                }

                @Override
                public Class<MapLoaded> event() {
                    return MapLoaded.class;
                }
            }
        };
    }

    /**
     * Get all triggers for a map
     */
    public Collection<CellAction> forMap(ExplorationMap map) {
        if (!triggers.containsKey(map.id())) {
            triggers.put(map.id(), repository.findByMap(map.id()));
        }

        return triggers.get(map.id()).stream()
            .map(actionFactory::create)
            .collect(Collectors.toList())
        ;
    }
}
