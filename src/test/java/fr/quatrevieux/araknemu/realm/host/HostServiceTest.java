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

package fr.quatrevieux.araknemu.realm.host;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class HostServiceTest extends RealmBaseCase {
    private HostService service;

    private boolean checkLogin = true;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new HostService(
            container.get(PlayerRepository.class)
        );

        dataSet.use(Player.class);
    }

    @Test
    void declare() {
        assertTrue(service.all().isEmpty());

        GameHost gh = new GameHost(new GameConnectorStub(), 1, 1234, "127.0.0.1");

        service.declare(gh);

        assertEquals(1, service.all().size());
        assertSame(gh, service.all().toArray()[0]);
    }

    @Test
    void updateState() {
        GameHost gh = new GameHost(new GameConnectorStub(), 1, 1234, "127.0.0.1");
        gh.setCanLog(true);
        service.declare(gh);

        service.updateHost(1, GameHost.State.SAVING, false);

        assertSame(GameHost.State.SAVING, gh.state());
        assertFalse(gh.canLog());
    }

    @Test
    void checkLoginWithNoHosts() throws ContainerException {
        service.checkLogin(
            new AuthenticationAccount(
                new Account(1),
                container.get(AuthenticationService.class)
            ),
            response -> checkLogin = response
        );

        assertFalse(checkLogin);
    }

    @Test
    void checkLoginWithOneHostsNotLogged() throws ContainerException {
        GameConnectorStub connector = new GameConnectorStub();
        GameHost gh = new GameHost(connector, 1, 1234, "127.0.0.1");
        service.declare(gh);

        connector.checkLogin = false;

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector.account);
        assertFalse(checkLogin);
    }

    @Test
    void checkLoginWithOneHostsLogged() throws ContainerException {
        GameConnectorStub connector = new GameConnectorStub();
        GameHost gh = new GameHost(connector, 1, 1234, "127.0.0.1");
        service.declare(gh);

        connector.checkLogin = true;

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector.account);
        assertTrue(checkLogin);
    }

    @Test
    void checkLoginWithMultipleHostsNotLogged() throws ContainerException {
        GameConnectorStub connector1 = new GameConnectorStub();
        GameHost gh1 = new GameHost(connector1, 1, 1234, "127.0.0.1");
        service.declare(gh1);

        GameConnectorStub connector2 = new GameConnectorStub();
        GameHost gh2 = new GameHost(connector2, 2, 4567, "127.0.0.1");
        service.declare(gh2);

        GameConnectorStub connector3 = new GameConnectorStub();
        GameHost gh3 = new GameHost(connector3, 3, 7891, "127.0.0.1");
        service.declare(gh3);

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector1.account);
        assertSame(account, connector2.account);
        assertSame(account, connector3.account);
        assertFalse(checkLogin);
    }

    @Test
    void checkLoginWithMultipleHostsLogged() throws ContainerException {
        GameConnectorStub connector1 = new GameConnectorStub();
        GameHost gh1 = new GameHost(connector1, 1, 1234, "127.0.0.1");
        service.declare(gh1);

        GameConnectorStub connector2 = new GameConnectorStub();
        GameHost gh2 = new GameHost(connector2, 2, 4567, "127.0.0.1");
        service.declare(gh2);
        connector2.checkLogin = true;

        GameConnectorStub connector3 = new GameConnectorStub();
        GameHost gh3 = new GameHost(connector3, 3, 7891, "127.0.0.1");
        service.declare(gh3);

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector1.account);
        assertSame(account, connector2.account);
        assertSame(account, connector3.account);
        assertTrue(checkLogin);
    }

    @Test
    void charactersByHost() throws ContainerException {
        dataSet.push(Player.forCreation(1, 1, "bob", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "cc", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "dd", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 3, "other", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(2, 3, "bad_account", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));

        Collection<ServerCharacters> serverCharacters = service.charactersByHost(
            new AuthenticationAccount(
                new Account(1),
                container.get(AuthenticationService.class)
            )
        );

        assertEquals(2, serverCharacters.size());

        ServerCharacters[] arr = serverCharacters.toArray(new ServerCharacters[]{});

        assertEquals(1, arr[0].serverId());
        assertEquals(3, arr[0].charactersCount());

        assertEquals(3, arr[1].serverId());
        assertEquals(1, arr[1].charactersCount());
    }

    @Test
    void searchFriendServers() throws ContainerException {
        dataSet.push(new Account(1, "john", "", "john"));
        dataSet.push(new Account(1, "other", "", "other"));
        dataSet.push(Player.forCreation(1, 1, "bob", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "cc", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "dd", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 3, "other", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(2, 3, "bad_account", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));

        Collection<ServerCharacters> serverCharacters = service.searchFriendServers("john");

        assertEquals(2, serverCharacters.size());

        ServerCharacters[] arr = serverCharacters.toArray(new ServerCharacters[]{});

        assertEquals(1, arr[0].serverId());
        assertEquals(3, arr[0].charactersCount());

        assertEquals(3, arr[1].serverId());
        assertEquals(1, arr[1].charactersCount());

        assertEquals(1, service.searchFriendServers("other").size());
    }
}
