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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.CloseSession;
import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.exception.WritePacket;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Configure base exception for a game session
 */
public final class GameExceptionConfigurator implements SessionConfigurator.Configurator<GameSession> {
    private final Logger logger;

    public GameExceptionConfigurator(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void configure(ConfigurableSession inner, GameSession session) {
        inner.addExceptionHandler(WritePacket.class, cause -> {
            session.send(cause.packet());

            if (cause instanceof Exception) {
                final Exception ex = (Exception) cause;

                if (ex.getCause() != null) {
                    logger.warn(MarkerManager.getMarker("ERROR_PACKET"), "[{}] Error packet caused by : {}", session, NullnessUtil.castNonNull(ex.getCause()).toString());
                }
            }

            return true;
        });

        inner.addExceptionHandler(CloseImmediately.class, cause -> {
            logger.error(MarkerManager.getMarker("CLOSE_IMMEDIATELY"), "[{}] Session closed : {}", session, cause.getMessage() == null ? cause.toString() : cause.getMessage());

            return true;
        });

        inner.addExceptionHandler(CloseSession.class, cause -> {
            session.close();

            return true;
        });

        inner.addExceptionHandler(RateLimitException.class, cause -> {
            logger.error(MarkerManager.getMarker("RATE_LIMIT"), "[{}] RateLimit : close session", session);
            session.close();

            return true;
        });
    }
}
