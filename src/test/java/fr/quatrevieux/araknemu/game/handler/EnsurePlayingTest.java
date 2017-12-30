package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.account.ListCharacters;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnsurePlayingTest extends GameBaseCase {
    @Test
    void handleNoPlayer() {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsurePlaying handler = new EnsurePlaying<>(inner);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void handleSuccess() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsurePlaying handler = new EnsurePlaying<>(inner);
        session.attach(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class),
                1
            )
        );
        session.setPlayer(
            new GamePlayer(
                session.account(),
                new Player(1, 1, 2, "Bob", Race.FECA, Sex.MALE, new Colors(-1,-1,-1), 5, null),
                new PlayerRace(Race.FECA, "Feca", new DefaultCharacteristics()),
                session
            )
        );

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(inner).handle(session, packet);
    }

    @Test
    void packet() throws ContainerException {
        assertEquals(
            AskCharacterList.class,
            new EnsurePlaying<>(new ListCharacters(
                container.get(CharactersService.class)
            )).packet()
        );
    }
}
