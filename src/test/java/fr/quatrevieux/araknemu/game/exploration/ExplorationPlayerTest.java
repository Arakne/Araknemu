package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.MapChanged;
import fr.quatrevieux.araknemu.game.exploration.event.MapLeaved;
import fr.quatrevieux.araknemu.game.exploration.event.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.PlayerSprite;
import fr.quatrevieux.araknemu.game.player.characteristic.Life;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
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
        session.setExploration(player);
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
        Listener<MapLoaded> listener = new Listener<MapLoaded>() {
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
        Listener<MapLeaved> listener = new Listener<MapLeaved>() {
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

    @Test
    void changeCell() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10300);
        player.join(map);

        player.changeCell(741);

        assertEquals(741, player.position().cell());

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(player.sprite())
            )
        );
    }

    @Test
    void changeMap() throws ContainerException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10300);

        AtomicReference<MapLoaded> ref1 = new AtomicReference<>();
        AtomicReference<MapChanged> ref2 = new AtomicReference<>();

        player.dispatcher().add(new Listener<MapLoaded>() {
            @Override
            public void on(MapLoaded event) {
                ref1.set(event);
            }

            @Override
            public Class<MapLoaded> event() {
                return MapLoaded.class;
            }
        });
        player.dispatcher().add(MapChanged.class, ref2::set);

        try {
            player.changeMap(map, 85);
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        assertSame(map, player.map());
        assertSame(map, ref1.get().map());
        assertSame(map, ref2.get().map());
        assertEquals(new Position(10300, 85), player.position());
    }

    @Test
    void inventory() {
        assertInstanceOf(PlayerInventory.class, player.inventory());
    }

    @Test
    void life() {
        assertInstanceOf(Life.class, player.life());
    }

    @Test
    void interactions() {
        assertFalse(player.interactions().busy());
    }

    @Test
    void player() throws SQLException, ContainerException {
        assertSame(gamePlayer(), player.player());
    }

    @Test
    void stopExploring() {
        session.setExploration(player);

        player.stopExploring();

        assertNull(session.exploration());
    }
}
