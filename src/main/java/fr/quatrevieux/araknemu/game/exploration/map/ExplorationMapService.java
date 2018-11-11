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
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.listener.map.fight.*;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle exploration maps
 */
final public class ExplorationMapService implements PreloadableService, EventsSubscriber {
    final private MapTemplateRepository repository;
    final private FightService fightService;
    final private Dispatcher dispatcher;
    final private CellLoader loader;

    final private ConcurrentMap<Integer, ExplorationMap> maps = new ConcurrentHashMap<>();

    public ExplorationMapService(MapTemplateRepository repository, FightService fightService, Dispatcher dispatcher, CellLoader loader) {
        this.repository = repository;
        this.fightService = fightService;
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
            },

            new SendCreatedFight(this, fightService),
            new Listener<FightCreated>() {
                @Override
                public void on(FightCreated event) {
                    ExplorationMap map = load(event.fight().map().id());

                    event.fight().dispatcher().add(new HideFightOnStart(map));
                    event.fight().dispatcher().add(new SendFightsCount(map, fightService));
                    event.fight().dispatcher().add(new SendCancelledFight(map, fightService));
                    event.fight().dispatcher().add(new SendTeamFighterRemoved(map));
                    event.fight().dispatcher().add(new SendTeamFighterAdded(map));
                }

                @Override
                public Class<FightCreated> event() {
                    return FightCreated.class;
                }
            },
        };
    }

    private ExplorationMap createMap(MapTemplate template) {
        ExplorationMap map = new ExplorationMap(template, loader);
        maps.put(map.id(), map);

        map.dispatcher().add(new SendNewSprite(map));
        map.dispatcher().add(new SendSpriteRemoved(map));
        map.dispatcher().add(new SendPlayerChangeCell(map));

        dispatcher.dispatch(new MapLoaded(map));

        return map;
    }
}
