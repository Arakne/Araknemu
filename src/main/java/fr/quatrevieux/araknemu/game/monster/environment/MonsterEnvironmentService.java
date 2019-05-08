package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.listener.map.monster.LaunchMonsterFight;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handle environment interactions with monsters
 */
final public class MonsterEnvironmentService implements EventsSubscriber, PreloadableService {
    final private FightService fightService;
    final private MonsterGroupFactory factory;
    final private MonsterGroupPositionRepository positionRepository;
    final private MonsterGroupDataRepository dataRepository;

    /**
     * Groups indexed by map id
     */
    final private ConcurrentMap<Integer, Collection<LivingMonsterGroupPosition>> groupsByMap = new ConcurrentHashMap<>();

    /**
     * Groups are preloaded ?
     * If true, consider that all data are loaded, so loading maps without loaded groups do not execute a query
     */
    private boolean preloaded = false;


    public MonsterEnvironmentService(FightService fightService, MonsterGroupFactory factory, MonsterGroupPositionRepository positionRepository, MonsterGroupDataRepository dataRepository) {
        this.fightService = fightService;
        this.factory = factory;
        this.positionRepository = positionRepository;
        this.dataRepository = dataRepository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading monster groups data...");

        final Map<Integer, MonsterGroupData> groupsData = dataRepository.all().stream()
            .collect(Collectors.toMap(MonsterGroupData::id, Function.identity()))
        ;

        logger.info("{} monster groups loaded", groupsData.size());

        logger.info("Loading monster groups positions...");

        for (MonsterGroupPosition position : positionRepository.all()) {
            groupsByMap
                .computeIfAbsent(position.position().map(), mapId -> new ArrayList<>())
                .add(new LivingMonsterGroupPosition(
                    factory,
                    fightService,
                    groupsData.get(position.groupId()),
                    SpawnCellSelector.forPosition(position.position())
                ))
            ;
        }

        logger.info("{} Map positions loaded", groupsByMap.size());

        preloaded = true;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<MapLoaded>() {
                @Override
                public void on(MapLoaded event) {
                    byMap(event.map().id()).forEach(group -> group.populate(event.map()));
                    event.map().dispatcher().add(new LaunchMonsterFight());
                }

                @Override
                public Class<MapLoaded> event() {
                    return MapLoaded.class;
                }
            }
        };
    }

    /**
     * Get list of groups for a map
     *
     * @param mapId The map id to load
     */
    public Collection<LivingMonsterGroupPosition> byMap(int mapId) {
        if (groupsByMap.containsKey(mapId)) {
            return groupsByMap.get(mapId);
        }

        if (preloaded) {
            return Collections.emptyList();
        }

        Collection<LivingMonsterGroupPosition> groups = new ArrayList<>();

        for (MonsterGroupPosition position : positionRepository.byMap(mapId)) {
            groups.add(new LivingMonsterGroupPosition(
                factory,
                fightService,
                dataRepository.get(position.groupId()),
                SpawnCellSelector.forPosition(position.position())
            ));
        }

        groupsByMap.put(mapId, groups);

        return groups;
    }
}
