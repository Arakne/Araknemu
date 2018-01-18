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
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.Test;

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
    void player() throws ContainerException {
        GameSession session = new GameSession(new DummyChannel());

        GamePlayer player = new GamePlayer(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class),
                2
            ),
            new Player(1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null),
            new PlayerRace(Race.FECA, "Feca", new DefaultCharacteristics(), new Position(10300, 308)),
            session,
            container.get(PlayerService.class)
        );

        session.setPlayer(player);

        assertSame(player, session.player());
    }

    @Test
    void exploration() throws ContainerException {
        GameSession session = new GameSession(new DummyChannel());

        ExplorationPlayer player = new ExplorationPlayer(
                new GamePlayer(
                new GameAccount(
                    new Account(1),
                    container.get(AccountService.class),
                    2
                ),
                new Player(1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23, null),
                new PlayerRace(Race.FECA, "Feca", new DefaultCharacteristics(), new Position(10300, 308)),
                session,
                container.get(PlayerService.class)
            )
        );

        session.setExploration(player);

        assertSame(player, session.exploration());
    }
}