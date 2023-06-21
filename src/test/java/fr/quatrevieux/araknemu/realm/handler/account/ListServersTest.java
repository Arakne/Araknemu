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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import fr.quatrevieux.araknemu.network.realm.in.AskServerList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class ListServersTest extends RealmBaseCase {
    private ListServers handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ListServers(
            container.get(HostService.class)
        );

        session.attach(
            new AuthenticationAccount(
                new Account(1),
                new PlainTextHash().parse("password"),
                container.get(AuthenticationService.class)
            )
        );

        dataSet.use(Player.class);
    }

    @Test
    void handleEmptyList() {
        handler.handle(session, new AskServerList());

        requestStack.assertLast(
            new ServerList(ServerList.ONE_YEAR, Collections.EMPTY_LIST)
        );
    }

    @Test
    void handleOneChar() throws ContainerException {
        dataSet.push(
            Player.forCreation(1, 1, "bob", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1))
        );

        handler.handle(session, new AskServerList());

        requestStack.assertLast(
            new ServerList(
                ServerList.ONE_YEAR,
                Collections.singleton(
                    new ServerCharacters(1, 1)
                )
            )
        );
    }

    @Test
    void handleMultipleChars() throws ContainerException {
        dataSet.push(Player.forCreation(1, 1, "bob", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "cc", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "dd", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 3, "other", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));

        handler.handle(session, new AskServerList());

        requestStack.assertLast(
            new ServerList(
                ServerList.ONE_YEAR,
                Arrays.asList(
                    new ServerCharacters(1, 3),
                    new ServerCharacters(3, 1)
                )
            )
        );
    }
}
