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

package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.core.network.Channel;
import fr.quatrevieux.araknemu.core.network.SessionHandler;
import fr.quatrevieux.araknemu.core.network.exception.CloseSession;
import fr.quatrevieux.araknemu.core.network.exception.InactivityTimeout;
import fr.quatrevieux.araknemu.core.network.exception.WritePacket;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

/**
 * IoHandler for Game server
 */
final public class GameSessionHandler implements SessionHandler<GameSession> {
    final private Dispatcher<GameSession> dispatcher;
    final private PacketParser parser;

    public GameSessionHandler(Dispatcher<GameSession> dispatcher, PacketParser parser) {
        this.dispatcher = dispatcher;
        this.parser = parser;
    }

    @Override
    public void opened(GameSession session) throws Exception {
        dispatcher.dispatch(session, new SessionCreated());
    }

    @Override
    public void closed(GameSession session) throws Exception {
        dispatcher.dispatch(session, new SessionClosed());
    }

    @Override
    public void exception(GameSession session, Throwable cause) {
        if (cause instanceof InactivityTimeout) {
            session.write(ServerMessage.inactivity());
            session.close();
            return;
        }

        if (cause instanceof WritePacket) {
            session.write(
                ((WritePacket) cause).packet()
            );
        }

        if (cause instanceof CloseSession) {
            session.close();
        }
    }

    @Override
    public void received(GameSession session, String message) throws Exception {
        dispatcher.dispatch(session, parser.parse(message));
    }

    @Override
    public void sent(GameSession session, Object message) throws Exception {}

    @Override
    public GameSession create(Channel channel) {
        return new GameSession(channel);
    }
}
