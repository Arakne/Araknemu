package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.MapLeaved;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.PlayerSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationPlayerTest extends GameBaseCase {
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        player = new ExplorationPlayer(gamePlayer());
    }

    @Test
    void sprite() throws SQLException, ContainerException {
        assertEquals(
            new PlayerSprite(gamePlayer()).toString(),
            player.sprite().toString()
        );
    }

    @Test
    void join() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10300);

        AtomicReference<ExplorationMap> ref = new AtomicReference<>();
        Listener<MapLoaded> listener = new Listener<>() {
            @Override
            public void on(MapLoaded event) {
                ref.set(event.map());
            }

            @Override
            public Class<MapLoaded> event() {
                return MapLoaded.class;
            }
        };

        player.dispatcher().add(listener);
        player.join(map);

        assertSame(map, player.map());
        assertSame(map, ref.get());
    }

    @Test
    void move() {
        player.move(123);

        assertEquals(123, player.cell());
        assertEquals(123, player.position().cell());
    }

    @Test
    void leave() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10300);
        player.join(map);

        AtomicReference<ExplorationMap> ref = new AtomicReference<>();
        Listener<MapLeaved> listener = new Listener<>() {
            @Override
            public void on(MapLeaved event) {
                ref.set(event.map());
            }

            @Override
            public Class<MapLeaved> event() {
                return MapLeaved.class;
            }
        };

        player.dispatcher().add(listener);

        player.leave();

        assertFalse(map.players().contains(player));
        assertSame(map, ref.get());
    }
}
