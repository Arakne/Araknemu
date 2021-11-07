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

package fr.quatrevieux.araknemu.game.listener.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.event.AccountPermissionsUpdated;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsGranted;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsRevoked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendAdminAccessTest extends GameBaseCase {
    private SendAdminAccess listener;
    private GameAccount account;
    private GameAccount performer;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendAdminAccess();
        account = gamePlayer().account();
        performer = makeSimpleGamePlayer(10, server.createSession(), true).account();
    }

    @Test
    void onPermissionsGranted() throws Exception {
        account.grant(Permission.MANAGE_PLAYER);
        requestStack.clear();

        listener.on(new AccountPermissionsUpdated(performer, account));

        requestStack.assertLast(new TemporaryRightsGranted("ACCOUNT_10"));
    }

    @Test
    void onPermissionsGrantedWithoutPerformer() throws Exception {
        account.grant(Permission.MANAGE_PLAYER);
        requestStack.clear();

        listener.on(new AccountPermissionsUpdated(null, account));

        requestStack.assertLast(new TemporaryRightsGranted("System"));
    }

    @Test
    void onPermissionsRevoked() throws Exception {
        requestStack.clear();

        listener.on(new AccountPermissionsUpdated(performer, account));

        requestStack.assertLast(new TemporaryRightsRevoked("ACCOUNT_10"));
    }

    @Test
    void onPermissionsRevokedWithoutPerformer() throws Exception {
        requestStack.clear();

        listener.on(new AccountPermissionsUpdated(null, account));

        requestStack.assertLast(new TemporaryRightsRevoked("System"));
    }
}
