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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.common.account.banishment.listener;

import fr.quatrevieux.araknemu.common.account.banishment.event.IpBanned;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import inet.ipaddr.IPAddressString;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Kick all session matching with the banned IP address
 */
public final class KickBannedIpSession implements Listener<IpBanned> {
    private final Supplier<Collection<? extends Session>> sessionsSupplier;

    public KickBannedIpSession(Supplier<Collection<? extends Session>> sessionsSupplier) {
        this.sessionsSupplier = sessionsSupplier;
    }

    @Override
    public void on(IpBanned event) {
        sessionsSupplier.get().forEach(session -> {
            if (event.rule().ipAddress().contains(new IPAddressString(session.channel().address().getAddress().getHostAddress()))) {
                session.send(new LoginError(LoginError.BANNED));
                session.close();
            }
        });
    }

    @Override
    public Class<IpBanned> event() {
        return IpBanned.class;
    }
}
