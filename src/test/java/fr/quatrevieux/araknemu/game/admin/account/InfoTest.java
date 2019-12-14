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

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.EnumSet;

class InfoTest extends CommandTestCase {
    @Test
    void executeSimple() throws ContainerException, AdminException, SQLException {
        command = new Info(
            container.get(AccountService.class).load(dataSet.push(new Account(-1, "azerty", "", "uiop"))),
            container.get(AccountRepository.class)
        );

        execute("info");

        assertOutput(
            "Account info : azerty",
            "=================================",
            "Name:   azerty",
            "Pseudo: uiop",
            "ID:     1",
            "Logged: No",
            "Standard account"
        );
    }

    @Test
    void executeLogged() throws ContainerException, AdminException, SQLException {
        GameAccount account = container.get(AccountService.class).load(dataSet.push(new Account(-1, "azerty", "", "uiop")));

        command = new Info(
            account,
            container.get(AccountRepository.class)
        );

        account.attach((GameSession) container.get(SessionFactory.class).create(new DummyChannel()));

        execute("info");

        assertOutputContains("Logged: Yes");
    }

    @Test
    void executeAdmin() throws ContainerException, AdminException, SQLException {
        GameAccount account = container.get(AccountService.class).load(dataSet.push(new Account(-1, "azerty", "", "uiop", EnumSet.of(Permission.ACCESS, Permission.MANAGE_ACCOUNT), "", "")));

        command = new Info(
            account,
            container.get(AccountRepository.class)
        );

        execute("info");

        assertOutputContains("Admin account");
        assertOutputContains("Permissions: [ACCESS, MANAGE_ACCOUNT]");
    }
}
