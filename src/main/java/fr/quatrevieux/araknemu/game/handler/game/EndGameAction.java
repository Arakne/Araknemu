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

package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.handler.AbstractExploringPacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import org.apache.logging.log4j.Logger;

/**
 * End the current game action with success
 */
public final class EndGameAction extends AbstractExploringPacketHandler<GameActionAcknowledge> {
    private final Logger logger;

    public EndGameAction(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handle(GameSession session, ExplorationPlayer exploration, GameActionAcknowledge packet) throws Exception {
        final boolean success = exploration
            .interactions()
            .end(packet.actionId())
        ;

        if (!success) {
            logger.warn("Failed to end game action {}", packet.actionId());
            session.send(new Noop());
        }
    }

    @Override
    public Class<GameActionAcknowledge> packet() {
        return GameActionAcknowledge.class;
    }
}
