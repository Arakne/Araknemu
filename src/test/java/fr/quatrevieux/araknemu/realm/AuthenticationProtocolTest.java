/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.out.BadVersion;
import fr.quatrevieux.araknemu.network.realm.out.Community;
import fr.quatrevieux.araknemu.network.realm.out.GMLevel;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import fr.quatrevieux.araknemu.network.realm.out.Pseudo;
import fr.quatrevieux.araknemu.network.realm.out.Question;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerPlain;
import inet.ipaddr.IPAddressString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationProtocolTest extends RealmBaseCase {
    private Account account;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        account = dataSet
            .use(ConnectionLog.class, Banishment.class)
            .push(new Account(-1, "test", "password", "pseudo", EnumSet.noneOf(Permission.class), "security question", "secret answer"), "test_account")
        ;
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
    void failBanned() throws Exception {
        dataSet.push(new Banishment(Account.class.cast(dataSet.get("test_account")).id(), Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "test", 3));

        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        requestStack.assertLast(new LoginError(LoginError.BANNED));
        assertClosed();
    }

    @Test
    void failIpBanned() {
        container.get(BanIpService.class).newRule(new IPAddressString("36.25.14.78")).apply();

        RealmSession session = sessionHandler.create(new DummyChannel("36.25.14.78"));

        assertEquals(new LoginError(LoginError.BANNED).toString(), ((DummyChannel)session.channel()).getMessages().peek().toString());
        assertFalse(session.isAlive());
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
            new Question("security question"),
            "AH1;1;110;1"
        );

        assertTrue(dataSet.refresh(account).password().startsWith("$argon2id$v=19$m=65536,t=4,p=8$"));
    }

    @Test
    void authenticationSuccessWithArgon2Password() throws Exception {
        AccountRepository repository = container.get(AccountRepository.class);
        account.setPassword("$argon2id$v=19$m=65536,t=4,p=8$wNluVjglTHANEbFv1QqVUg$z6yjCMpUkyWiQ4V8hDTQGPFvZb/pDfge78Pcmn3uUoU");
        repository.savePassword(account);

        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        assertTrue(session.isLogged());
        assertEquals("pseudo", session.account().pseudo());

        requestStack.assertAll(
            new Pseudo("pseudo"),
            new Community(0),
            new GMLevel(false),
            new Question("security question"),
            "AH1;1;110;1"
        );
    }

    @Test
    void authenticateTwiceError() throws Exception {
        RealmSession s1 = sessionHandler.create(new DummyChannel());

        s1.receive("1.29.1");
        s1.receive("test\n#1"+ConnectionKeyTest.cryptPassword("password", s1.key().key()));

        assertTrue(s1.isLogged());
        assertTrue(s1.account().isLogged());

        // Authenticate with second session on same account
        sendPacket("1.29.1");
        sendPacket("test\n#1"+ConnectionKeyTest.cryptPassword("password", session.key().key()));

        assertFalse(session.isLogged());
        requestStack.assertLast(new LoginError(LoginError.ALREADY_LOGGED));

        assertTrue(s1.isLogged());
        assertTrue(s1.account().isLogged());
    }

    @Test
    void authenticateAndLogout() throws Exception {
        RealmSession s1 = sessionHandler.create(new DummyChannel());

        s1.receive("1.29.1");
        s1.receive("test\n#1"+ConnectionKeyTest.cryptPassword("password", s1.key().key()));
        s1.close();

        assertFalse(s1.account().isLogged());

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
