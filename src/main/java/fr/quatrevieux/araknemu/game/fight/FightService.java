package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for create fights
 */
final public class FightService implements EventsSubscriber {
    final private MapTemplateRepository mapRepository;
    final private Dispatcher dispatcher;
    final private Map<Class, FightBuilderFactory> builderFactories;

    final private Map<Integer, Map<Integer, Fight>> fightsByMapId = new HashMap<>();
    final private AtomicInteger lastFightId = new AtomicInteger();

    public FightService(MapTemplateRepository mapRepository, Dispatcher dispatcher, Collection<? extends FightBuilderFactory> factories) {
        this.mapRepository = mapRepository;
        this.dispatcher = dispatcher;

        this.builderFactories = factories.stream().collect(
            Collectors.toMap(
                FightBuilderFactory::type,
                Function.identity()
            )
        );
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new AttachFighter(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
            new Listener<ExplorationPlayerCreated> () {
                @Override
                public void on(ExplorationPlayerCreated event) {
                    event.player().dispatcher().add(new LeaveExplorationForFight(event.player()));
                }

                @Override
                public Class<ExplorationPlayerCreated> event() {
                    return ExplorationPlayerCreated.class;
                }
            }
        };
    }

    /**
     * Create fight map
     *
     * @param map The base map
     */
    public FightMap map(ExplorationMap map) {
        return new FightMap(mapRepository.get(map.id()));
    }

    /**
     * Create the fight handler
     *
     * @param type The build type
     */
    @SuppressWarnings("unchecked")
    public <B extends FightBuilder> FightHandler<B> handler(Class<B> type) {
        return new FightHandler(
            this,
            builderFactories.get(type).create(this)
        );
    }

    /**
     * Get all fights on the map
     *
     * @param mapId The map id
     */
    public Collection<Fight> fightsByMap(int mapId) {
        if (fightsByMapId.containsKey(mapId)) {
            return fightsByMapId.get(mapId).values();
        }

        return Collections.emptyList();
    }

    /**
     * Get a fight by its id from a map
     *
     * @param mapId The map id
     * @param fightId The fight id
     */
    public Fight getFromMap(int mapId, int fightId) {
        if (!fightsByMapId.containsKey(mapId)) {
            throw new NoSuchElementException("Fight not found");
        }

        Map<Integer, Fight> fights = fightsByMapId.get(mapId);

        if (!fights.containsKey(fightId)) {
            throw new NoSuchElementException("Fight not found");
        }

        return fights.get(fightId);
    }

    /**
     * Generate a new unique fight id
     */
    int newFightId() {
        return lastFightId.incrementAndGet();
    }

    /**
     * The fight is initialized
     */
    synchronized void created(Fight fight) {
        if (fightsByMapId.containsKey(fight.map().id())) {
            fightsByMapId.get(fight.map().id()).put(fight.id(), fight);
        } else {
            Map<Integer, Fight> fights = new HashMap<>();

            fights.put(fight.id(), fight);

            fightsByMapId.put(fight.map().id(), fights);
        }

        dispatcher.dispatch(new FightCreated(fight));
    }

    /**
     * Remove the fight
     */
    synchronized void remove(Fight fight) {
        fightsByMapId.get(fight.map().id()).remove(fight.id());
    }
}
