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

package fr.quatrevieux.araknemu.common.account.banishment.network;

import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.network.realm.out.LoginError;
import inet.ipaddr.IPAddressString;

/**
 * Check if the ip address of session is banned during creation
 *
 * @param <S> The session type
 */
final public class BanIpCheck<S extends Session> implements SessionConfigurator.Configurator<S> {
    final private BanIpService service;

    public BanIpCheck(BanIpService service) {
        this.service = service;
    }

    @Override
    public void configure(ConfigurableSession inner, S session) {
        if (service.isIpBanned(new IPAddressString(session.channel().address().getAddress().getHostAddress()))) {
            session.send(new LoginError(LoginError.BANNED));
            session.close();
        }
    }
}
