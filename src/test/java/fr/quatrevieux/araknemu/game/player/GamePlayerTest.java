package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerTest extends GameBaseCase {
    private GamePlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushRaces()
        ;

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.INTELLIGENCE, 150);
        characteristics.set(Characteristic.VITALITY, 50);

        player = new GamePlayer(
            new GameAccount(
                new Account(2),
                container.get(AccountService.class),
                1
            ),
            new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 50, characteristics, new Position(10300, 308)),
            dataSet.refresh(new PlayerRace(Race.CRA)),
            session
        );
    }

    @Test
    void send() {
        player.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void characteristics() {
        assertEquals(150, player.characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(1, player.characteristics().get(Characteristic.INITIATIVE));
        assertEquals(6, player.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void position() {
        assertEquals(new Position(10300, 308), player.position());
    }

    @Test
    void sprite() {
        assertEquals(
            new PlayerSprite(player).toString(),
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
}
