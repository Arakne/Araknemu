package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
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
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest extends GameBaseCase {
    @Test
    void account() throws ContainerException {
        GameSession session = new GameSession(new DummyChannel());

        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        session.attach(account);
        assertSame(account, session.account());
    }

    @Test
    void player() throws ContainerException, SQLException {
        GameSession session = new GameSession(new DummyChannel());

        GamePlayer player = makeSimpleGamePlayer(10);

        session.setPlayer(player);

        assertSame(player, session.player());
    }

    @Test
    void exploration() throws ContainerException, SQLException {
        GameSession session = new GameSession(new DummyChannel());

        ExplorationPlayer player = new ExplorationPlayer(gamePlayer());

        session.setExploration(player);

        assertSame(player, session.exploration());
    }

    @Test
    void dispatchWithPlayer() throws ContainerException, SQLException {
        GameSession session = new GameSession(new DummyChannel());
        GamePlayer player = makeSimpleGamePlayer(10);
        session.setPlayer(player);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        CharacteristicsChanged event = new CharacteristicsChanged();
        session.dispatch(event);

        assertSame(event, ref.get());
    }

    @Test
    void dispatchWithExploration() throws ContainerException, SQLException {
        GameSession session = new GameSession(new DummyChannel());
        GamePlayer player = makeSimpleGamePlayer(10);
        session.setPlayer(player);

        ExplorationPlayer exploration = explorationPlayer();
        session.setExploration(exploration);

        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        player.dispatcher().add(CharacteristicsChanged.class, ref::set);

        AtomicReference<CharacteristicsChanged> ref2 = new AtomicReference<>();
        exploration.dispatcher().add(CharacteristicsChanged.class, ref2::set);

        CharacteristicsChanged event = new CharacteristicsChanged();
        session.dispatch(event);

        assertSame(event, ref.get());
        assertSame(event, ref2.get());
    }
}
