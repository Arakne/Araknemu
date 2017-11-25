package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.out.*;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationProtocolTest extends RealmBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.push(new Account(-1, "test", "password", "pseudo"), "test_account");
    }

    @Test
    void failWithBadVersion() throws Exception {
        sendPacket("1.0.4");

        requestStack.assertLast(new BadVersion("1.29.1"));
        assertClosed();
    }

    @Test
    void failWithBadCredentials() throws Exception {
        sendPacket("1.29.1");
        sendPacket("login\n#1password");

        requestStack.assertLast(new LoginError(LoginError.LOGIN_ERROR));
        assertClosed();
    }

    @Test
    void failBadPassword() throws Exception {
        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("bad_password", session.key().key()));

        requestStack.assertLast(new LoginError(LoginError.LOGIN_ERROR));
        assertClosed();
    }

    @Test
    void authenticationSuccess() throws Exception {
        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        assertTrue(session.isLogged());
        assertEquals("pseudo", session.account().pseudo());

        requestStack.assertAll(
            new Pseudo("pseudo"),
            new Community(0),
            new GMLevel(false),
            new Answer(""),
            "AH1;1;110;1"
        );
    }

    @Test
    void authenticateTwiceError() throws Exception {
        IoSession io = new DummySession();
        io.setAttribute("testing");
        RealmSession s1 = new RealmSession(io);

        ioHandler.messageReceived(io, "1.29.1");
        ioHandler.messageReceived(io,"test\n#1"+ConnectionKeyTest.cryptPassword("password", s1.key().key()));

        assertTrue(s1.isLogged());
        assertTrue(s1.account().isAlive());

        // Authenticate with second session on same account
        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        assertFalse(session.isLogged());
        requestStack.assertLast(new LoginError(LoginError.ALREADY_LOGGED));

        assertTrue(s1.isLogged());
        assertTrue(s1.account().isAlive());
    }

    @Test
    void authenticateAndLogout() throws Exception {
        IoSession io = new DummySession();
        io.setAttribute("testing");
        RealmSession s1 = new RealmSession(io);

        ioHandler.messageReceived(io, "1.29.1");
        ioHandler.messageReceived(io,"test\n#1"+ConnectionKeyTest.cryptPassword("password", s1.key().key()));
        s1.close();

        assertFalse(s1.account().isAlive());

        // Authenticate with second session on same account
        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        assertTrue(session.isLogged());
    }

    @Test
    void selectGameServer() throws Exception {
        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        connector.token = "my_token";
        sendPacket("AX1");

        requestStack.assertLast(new SelectServerPlain("127.0.0.1", 1234, "my_token"));
    }
}
