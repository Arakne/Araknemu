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

import fr.quatrevieux.araknemu.core.network.exception.CloseSession;
import fr.quatrevieux.araknemu.core.network.exception.InactivityTimeout;
import fr.quatrevieux.araknemu.core.network.exception.WritePacket;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

/**
 * Configure base exception for a game session
 */
final public class GameExceptionConfigurator implements SessionConfigurator.Configurator<GameSession> {
    @Override
    public void configure(ConfigurableSession inner, GameSession session) {
        inner.addExceptionHandler(InactivityTimeout.class, cause -> {
            session.send(ServerMessage.inactivity());
            session.close();

            return false;
        });

        inner.addExceptionHandler(WritePacket.class, cause -> {
            session.send(cause.packet());

            return true;
        });

        inner.addExceptionHandler(CloseSession.class, cause -> {
            session.close();

            return true;
        });
    }
}
