package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMapTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
    }

    @Test
    void data() throws ContainerException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template);

        assertEquals(10300, map.id());
        assertEquals(template.date(), map.date());
        assertEquals(template.key(), map.key());
    }

    @Test
    void addPlayerWillAddSprite() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template);

        assertEquals(0, map.sprites().size());

        map.add(explorationPlayer());

        assertEquals(1, map.sprites().size());
        assertEquals(explorationPlayer().sprite().toString(), map.sprites().toArray()[0].toString());
    }

    @Test
    void addPlayerWillDispatchEvent() throws SQLException, ContainerException {
        AtomicReference<NewSpriteOnMap> ref = new AtomicReference<>();

        Listener<NewSpriteOnMap> listener = new Listener<>() {
            @Override
            public void on(NewSpriteOnMap event) {
                ref.set(event);
            }

            @Override
            public Class<NewSpriteOnMap> event() {
                return NewSpriteOnMap.class;
            }
        };

        ExplorationPlayer player = explorationPlayer();

        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template);
        map.dispatcher().add(listener);

        map.add(player);

        assertNotNull(ref.get());
        assertEquals(player.sprite().toString(), ref.get().sprite().toString());

        ExplorationPlayer other = new ExplorationPlayer(
            new GamePlayer(
                new GameAccount(
                    new Account(2),
                    container.get(AccountService.class),
                    1
                ),
                new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1)),
                dataSet.refresh(new PlayerRace(Race.CRA)),
                new GameSession(new DummyChannel())
            )
        );

        map.add(other);

        assertEquals(other.sprite().toString(), ref.get().sprite().toString());
    }

    @Test
    void sendWillSendToPlayers() throws ContainerException, SQLException {
        MapTemplate template = dataSet.refresh(new MapTemplate(10300, null, null, null, null));

        ExplorationMap map = new ExplorationMap(template);

        ExplorationPlayer player = explorationPlayer();
        player.join(map);

        map.send("my packet");

        requestStack.assertLast("my packet");
    }

    @Test
    void removeWillSendPacket() throws ContainerException, SQLException {
        explorationPlayer();
        ExplorationPlayer other = new ExplorationPlayer(
            new GamePlayer(
                new GameAccount(
                    new Account(2),
                    container.get(AccountService.class),
                    1
                ),
                new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1)),
                dataSet.refresh(new PlayerRace(Race.CRA)),
                new GameSession(new DummyChannel())
            )
        );

        ExplorationMap map = explorationPlayer().map();

        other.join(map);
        map.remove(other);

        requestStack.assertLast(
            new RemoveSprite(other.sprite())
        );

        assertTrue(map.players().contains(explorationPlayer()));
        assertFalse(map.players().contains(other));
    }
}
