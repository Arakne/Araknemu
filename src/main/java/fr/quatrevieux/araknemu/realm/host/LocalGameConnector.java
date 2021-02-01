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

import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;

/**
 * GameConnector for local game server
 */
public final class LocalGameConnector implements GameConnector {
    private final ConnectorService service;

    public LocalGameConnector(ConnectorService service) {
        this.service = service;
    }

    @Override
    public void checkLogin(AuthenticationAccount account, HostResponse<Boolean> response) {
        response.response(
            service.isLogged(account.id())
        );
    }

    @Override
    public void token(AuthenticationAccount account, HostResponse<String> response) {
        response.response(
            service.token(account.id())
        );
    }
}
