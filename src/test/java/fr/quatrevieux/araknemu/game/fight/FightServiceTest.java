package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FightServiceTest extends GameBaseCase {
    private FightService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = new FightService(
            container.get(MapTemplateRepository.class),
            Arrays.asList(
                new ChallengeBuilderFactory()
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
}