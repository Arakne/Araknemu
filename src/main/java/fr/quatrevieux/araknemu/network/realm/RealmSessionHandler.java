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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.Channel;
import fr.quatrevieux.araknemu.core.network.SessionHandler;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.network.realm.in.RealmPacketParser;

/**
 * IoHandler for realm
 */
final public class RealmSessionHandler implements SessionHandler<RealmSession> {
    final private Dispatcher<RealmSession> dispatcher;
    final private PacketParser[] loginPackets;
    final private PacketParser baseParser;

    public RealmSessionHandler(Dispatcher<RealmSession> dispatcher, PacketParser[] loginPackets, PacketParser baseParser) {
        this.dispatcher = dispatcher;
        this.loginPackets = loginPackets;
        this.baseParser = baseParser;
    }

    @Override
    public void opened(RealmSession session) throws Exception {
        dispatcher.dispatch(session, new SessionCreated());
    }

    @Override
    public void closed(RealmSession session) throws Exception {
        dispatcher.dispatch(session, new SessionClosed());
    }

    @Override
    public void exception(RealmSession session, Throwable cause) {
        // If an error occurs before authenticate procedure
        // The session MUST be destroyed for security reasons
        if (!session.isLogged()) {
            session.close();
        }
    }

    @Override
    public void received(RealmSession session, String message) throws Exception {
        dispatcher.dispatch(session, session.parser().parse(message));
    }

    @Override
    public void sent(RealmSession session, Object message) throws Exception {}

    @Override
    public RealmSession create(Channel channel) {
        return new RealmSession(
            channel,
            new RealmPacketParser(
                loginPackets,
                baseParser
            )
        );
    }
}
