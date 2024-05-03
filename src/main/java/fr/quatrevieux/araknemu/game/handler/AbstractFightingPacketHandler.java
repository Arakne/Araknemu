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

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Base type for packet handlers that require the player to be in a fight
 *
 * @param <P> Packet to handler
 * @see GameSession#fighter() will be set
 */
public abstract class AbstractFightingPacketHandler<P extends Packet> implements PacketHandler<GameSession, P> {
    @Override
    public final void handle(GameSession session, P packet) {
        final PlayerFighter fighter = session.fighter();

        if (fighter == null) {
            throw new CloseImmediately("Not in fight");
        }

        fighter.fight().execute(() -> {
            final PlayerFighter currentFighter = session.fighter();

            // The player has left the fight before the execution of the action
            if (currentFighter == null) {
                return;
            }

            try {
                handle(session, fighter.fight(), fighter, packet);
            } catch (Exception e) {
                session.exception(e, packet);
            }
        });
    }

    /**
     * Handle the packet with a valid fighter
     *
     * @param session The session
     * @param fight The current fight
     * @param fighter The fighter
     * @param packet The packet to handle
     */
    protected abstract void handle(GameSession session, Fight fight, PlayerFighter fighter, P packet) throws Exception;
}
