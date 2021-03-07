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

import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;

/**
 * Configure packets handling for game session
 */
public final class GamePacketConfigurator implements SessionConfigurator.Configurator<GameSession> {
    private final Dispatcher<GameSession> dispatcher;
    private final PacketParser parser;

    public GamePacketConfigurator(Dispatcher<GameSession> dispatcher, PacketParser parser) {
        this.dispatcher = dispatcher;
        this.parser = parser;
    }

    @Override
    public void configure(ConfigurableSession inner, GameSession session) {
        inner.addReceiveMiddleware((packet, next) -> {
            if (!(packet instanceof Packet)) {
                packet = parser.parse(packet.toString());
            }

            next.accept(packet);
        });

        inner.addReceiveMiddleware((packet, next) -> dispatcher.dispatch(session, (Packet) packet));
    }
}
