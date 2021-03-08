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

package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;
import fr.quatrevieux.araknemu.realm.host.LocalGameConnector;

/**
 * GameConnector for local game server (i.e. Game and Realm on same application)
 */
public final class LocalRealmConnector implements RealmConnector {
    private final HostService realm;
    private final ConnectorService service;

    public LocalRealmConnector(HostService realm, ConnectorService service) {
        this.realm = realm;
        this.service = service;
    }

    @Override
    public void declare(int id, int port, String ip) {
        realm.declare(
            new GameHost(
                new LocalGameConnector(service),
                id,
                port,
                ip
            )
        );
    }

    @Override
    public void updateState(int id, GameHost.State state, boolean canLog) {
        realm.updateHost(id, state, canLog);
    }
}
