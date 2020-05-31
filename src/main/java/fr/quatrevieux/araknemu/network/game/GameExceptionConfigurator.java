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

package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.core.network.exception.CloseSession;
import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.exception.WritePacket;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import org.apache.logging.log4j.Logger;

/**
 * Configure base exception for a game session
 */
final public class GameExceptionConfigurator implements SessionConfigurator.Configurator<GameSession> {
    final private Logger logger;

    public GameExceptionConfigurator(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void configure(ConfigurableSession inner, GameSession session) {
        inner.addExceptionHandler(WritePacket.class, cause -> {
            session.send(cause.packet());

            return true;
        });

        inner.addExceptionHandler(CloseSession.class, cause -> {
            session.close();

            return true;
        });

        inner.addExceptionHandler(RateLimitException.class, cause -> {
            logger.error("[{}] RateLimit : close session", session.channel().id());
            session.close();

            return true;
        });
    }
}
