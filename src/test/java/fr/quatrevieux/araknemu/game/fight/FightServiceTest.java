package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.BaseBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.fight.module.RaulebaqueModule;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FightServiceTest extends FightBaseCase {
    private FightService service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = new FightService(
            container.get(MapTemplateRepository.class),
            dispatcher = new DefaultListenerAggregate(),
            Arrays.asList(
                new ChallengeBuilderFactory(container.get(FighterFactory.class), NOPLogger.NOP_LOGGER)
            ),
            Arrays.asList(
                RaulebaqueModule::new
            )
        );
    }

    @Test
    void map() throws ContainerException {
        assertNotNull(service.map(container.get(ExplorationMapService.class).load(10340)));
    }

    @Test
    void handler() {
        assertNotNull(service.handler(ChallengeBuilder.class));
    }

    @Test
    void playerLoadedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(AttachFighter.class));
    }

    @Test
    void explorationPlayerCreatedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        ExplorationPlayer player = new ExplorationPlayer(gamePlayer(true));
        dispatcher.dispatch(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(LeaveExplorationForFight.class));
    }

    @Test
    void newFightId() {
        assertEquals(1, service.newFightId());
        assertEquals(2, service.newFightId());
    }

    @Test
    void created() throws Exception {
        Fight fight = createFight(false);

        AtomicReference<FightCreated> ref = new AtomicReference<>();
        dispatcher.add(FightCreated.class, ref::set);

        service.created(fight);

        assertSame(fight, ref.get().fight());
        assertSame(fight, service.getFromMap(10340, 1));
        assertCollectionEquals(service.fightsByMap(10340), fight);
    }

    @Test
    void remove() throws Exception {
        Fight fight = createFight(false);

        service.created(fight);
        service.remove(fight);

        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void getFromMapInvalidMap() {
        assertThrows(NoSuchElementException.class, () -> service.getFromMap(0, 0));
    }

    @Test
    void getFromMapInvalidFightId() throws Exception {
        Fight fight = createFight(false);

        service.created(fight);

        assertThrows(NoSuchElementException.class, () -> service.getFromMap(10340, 0));
    }

    @Test
    void fightsByMapNoFights() {
        assertCount(0, service.fightsByMap(10340));
    }

    @Test
    void fightsByMap() throws ContainerException, SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight1 = createSimpleFight(map);
        Fight fight2 = createSimpleFight(map);
        Fight fight3 = createSimpleFight(map);

        service.created(fight1);
        service.created(fight2);
        service.created(fight3);

        assertCollectionEquals(service.fightsByMap(10340), fight1, fight2, fight3);
    }

    @Test
    void modules() throws ContainerException {
        BaseBuilder builder = new BaseBuilder(service, new RandomUtil(), new ChallengeType(), NOPLogger.NOP_LOGGER);
        builder.map(container.get(ExplorationMapService.class).load(10340));
        Fight fight = builder.build(1);

        Collection<FightModule> modules = service.modules(fight);

        assertCount(1, modules);
        assertContainsType(RaulebaqueModule.class, modules);
    }
}
