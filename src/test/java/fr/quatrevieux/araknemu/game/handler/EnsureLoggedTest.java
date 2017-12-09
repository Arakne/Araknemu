package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.account.ListCharacters;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class EnsureLoggedTest extends GameBaseCase {
    @Test
    void handleNotLogged() {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureLogged handler = new EnsureLogged(inner);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void handleSuccess() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureLogged handler = new EnsureLogged(inner);
        session.attach(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class),
                1
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
            new EnsureLogged<>(new ListCharacters(
                container.get(CharactersService.class)
            )).packet()
        );
    }
}
