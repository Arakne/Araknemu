package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.builder.FightHandler;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for create fights
 */
final public class FightService implements EventsSubscriber {
    final private MapTemplateRepository mapRepository;
    final private Map<Class, FightBuilderFactory> builderFactories;

    public FightService(MapTemplateRepository mapRepository, Collection<? extends FightBuilderFactory> factories) {
        this.mapRepository = mapRepository;
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
    public <B extends FightBuilder> FightHandler<B> handler(Class<B> type) {
        return new FightHandler(builderFactories.get(type).create(this));
    }
}
