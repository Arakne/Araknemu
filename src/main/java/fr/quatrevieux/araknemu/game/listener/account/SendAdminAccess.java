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

import fr.quatrevieux.araknemu.common.account.AbstractLivingAccount;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.account.event.AccountPermissionsUpdated;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsGranted;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.TemporaryRightsRevoked;

/**
 * Send to the client the current admin access of the account, when permissions are updated
 */
public final class SendAdminAccess implements Listener<AccountPermissionsUpdated> {
    @Override
    public void on(AccountPermissionsUpdated event) {
        event.account().session().ifPresent(session -> session.send(packet(event)));
    }

    @Override
    public Class<AccountPermissionsUpdated> event() {
        return AccountPermissionsUpdated.class;
    }

    private Object packet(AccountPermissionsUpdated event) {
        final String performer = event.performer().map(AbstractLivingAccount::pseudo).orElse("System");

        return event.authorized()
            ? new TemporaryRightsGranted(performer)
            : new TemporaryRightsRevoked(performer)
        ;
    }
}
