package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceTest extends GameBaseCase {
    private PlayerService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new PlayerService(
            container.get(PlayerRepository.class),
            container.get(PlayerRaceRepository.class),
            container.get(GameConfiguration.class),
            container.get(Dispatcher.class)
        );

        login();
        dataSet.pushRaces();
    }

    @Test
    void loadInvalidServer() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 3, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        assertThrows(EntityNotFoundException.class, () -> service.load(session, id));
    }

    @Test
    void loadSuccess() throws ContainerException, SQLException {
        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertEquals(id, player.id());
    }

    @Test
    void loadWillDispatchPlayerLoaded() throws ContainerException {
        AtomicReference<PlayerLoaded> ref = new AtomicReference<>();

        Listener<PlayerLoaded> listener = new Listener<PlayerLoaded>() {
            @Override
            public void on(PlayerLoaded event) {
                ref.set(event);
            }

            @Override
            public Class<PlayerLoaded> event() {
                return PlayerLoaded.class;
            }
        };

        container.get(ListenerAggregate.class).add(listener);

        int id = dataSet.push(new Player(-1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null)).id();

        GamePlayer player = service.load(session, id);

        assertSame(player, ref.get().player());
    }
}
