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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsGranted;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsRevoked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevokeTest extends CommandTestCase {
    private GameAccount account;
    private GameSession targetSession;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        targetSession = server.createSession();
        account = makeSimpleGamePlayer(10, targetSession, true).account();
        account.grant(Permission.ACCESS);
        command = new Revoke(account);
    }

    @Test
    void revokeSuccess() throws AdminException, SQLException {
        execute("revoke");

        new SendingRequestStack((DummyChannel) targetSession.channel()).assertLast(new TemporaryRightsRevoked("bob"));
        assertFalse(account.isMaster());
        assertFalse(account.isGranted(Permission.ACCESS));

        assertOutput("Permissions revoked for ACCOUNT_10");
    }

    @Test
    void notLoggedAccountShouldFailed() throws SQLException, AdminException {
        account = new GameAccount(new Account(11, "test", "test", "test"), container.get(AccountService.class), 2);
        command = new Revoke(account);

        execute("revoke");
        assertOutput("Cannot revoke permissions for test : the account is not logged");
    }

    @Test
    void help() {
        assertHelp(
            "revoke - Revoke all temporary permissions of an account",
            "========================================",
            "SYNOPSIS",
                "\trevoke",
            "EXAMPLES",
                "\t#Bob revoke  - Revoke permissions of Bob account",
                "\t@John revoke - Revoke permissions of the player John",
            "SEE ALSO",
                "\tgrant - For grant temporary permissions",
            "PERMISSIONS",
                "\t[ACCESS, SUPER_ADMIN]"
        );
    }
}
