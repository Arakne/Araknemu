package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenError;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameIoHandlerTest extends GameBaseCase {
    private GameIoHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new GameIoHandler(
            container.get(Dispatcher.class),
            container.get(PacketParser.class)
        );
    }

    @Test
    void sessionOpened() throws Exception {
        handler.sessionOpened(ioSession);

        requestStack.assertLast("HG");
    }

    @Test
    void sessionClosed() throws Exception {
        handler.sessionClosed(ioSession);
        assertFalse(session.isLogged());
    }

    @Test
    void exceptionCaughtCloseSession() throws Exception {
        handler.exceptionCaught(ioSession, new CloseImmediately());

        assertTrue(ioSession.isClosing());
    }

    @Test
    void exceptionCaughtWritePacket() throws Exception {
        handler.exceptionCaught(ioSession, new ErrorPacket(new LoginTokenError()));

        requestStack.assertLast(new LoginTokenError());
    }

    @Test
    void exceptionCaughtWriteAndClose() throws Exception {
        handler.exceptionCaught(ioSession, new CloseWithPacket(new LoginTokenError()));

        requestStack.assertLast(new LoginTokenError());
        assertTrue(ioSession.isClosing());
    }

    @Test
    void messageReceivedSuccess() throws Exception {
        Account account = new Account(1);
        dataSet.push(account);

        String token = container.get(TokenService.class).generate(account);

        handler.messageReceived(ioSession, "AT" + token);

        assertTrue(session.isLogged());
        assertEquals(1, session.account().id());
    }
}