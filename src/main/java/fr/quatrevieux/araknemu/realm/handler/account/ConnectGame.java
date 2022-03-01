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

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.ChooseServer;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerError;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerPlain;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Connect the client to choose game server
 *
 * @todo Do not use SelectServerPlain
 */
public final class ConnectGame implements PacketHandler<RealmSession, ChooseServer> {
    private final HostService service;

    public ConnectGame(HostService service) {
        this.service = service;
    }

    @Override
    public void handle(RealmSession session, ChooseServer packet) {
        if (!service.isAvailable(packet.id())) {
            session.send(
                new SelectServerError(SelectServerError.Error.CANT_SELECT)
            );

            return;
        }

        final GameHost host = service.get(packet.id());

        host.connector().token(
            NullnessUtil.castNonNull(session.account()),
            token -> {
                session.send(new SelectServerPlain(host.ip(), host.port(), token));
                session.close();
            }
        );
    }

    @Override
    public Class<ChooseServer> packet() {
        return ChooseServer.class;
    }
}
