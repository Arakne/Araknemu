package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle exploration maps
 */
final public class ExplorationMapService implements PreloadableService, EventsSubscriber {
    final private MapTemplateRepository repository;
    final private Dispatcher dispatcher;
    final private CellLoader loader;

    final private ConcurrentMap<Integer, ExplorationMap> maps = new ConcurrentHashMap<>();

    public ExplorationMapService(MapTemplateRepository repository, Dispatcher dispatcher, CellLoader loader) {
        this.repository = repository;
        this.dispatcher = dispatcher;
        this.loader = loader;
    }

    /**
     * Load the exploration map
     */
    public ExplorationMap load(int mapId) throws EntityNotFoundException {
        if (!maps.containsKey(mapId)) {
            return createMap(repository.get(mapId));
        }

        return maps.get(mapId);
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading maps...");

        long start = System.currentTimeMillis();

        repository.all().forEach(this::createMap);

        long time = System.currentTimeMillis() - start;

        logger.info("{} maps successfully loaded in {}ms", maps.size(), time);
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ExplorationPlayerCreated>() {
                @Override
                public void on(ExplorationPlayerCreated event) {
                    event.player().dispatcher().add(new SendMapData(event.player()));
                }

                @Override
                public Class<ExplorationPlayerCreated> event() {
                    return ExplorationPlayerCreated.class;
                }
            }
        };
    }

    private ExplorationMap createMap(MapTemplate template) {
        ExplorationMap map = new ExplorationMap(template, loader);
        maps.put(map.id(), map);

        map.dispatcher().add(new SendNewSprite(map));
        map.dispatcher().add(new ValidatePlayerPath());
        map.dispatcher().add(new SendPlayerMove(map));
        map.dispatcher().add(new SendSpriteRemoved(map));
        map.dispatcher().add(new SendPlayerChangeCell(map));

        dispatcher.dispatch(new MapLoaded(map));

        return map;
    }
}
