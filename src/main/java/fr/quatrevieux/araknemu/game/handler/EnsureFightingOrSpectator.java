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
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Ensure that the session contains a valid fighter or spectator, and execute the corresponding packet handler
 *
 * @param <P> Packet to handle
 */
public final class EnsureFightingOrSpectator<P extends Packet> implements PacketHandler<GameSession, P> {
    private final PacketHandler<GameSession, P> fightingHandler;
    private final PacketHandler<GameSession, P> spectatorHandler;

    public EnsureFightingOrSpectator(PacketHandler<GameSession, P> fightingHandler, PacketHandler<GameSession, P> spectatorHandler) {
        this.fightingHandler = fightingHandler;
        this.spectatorHandler = spectatorHandler;
    }

    @Override
    public void handle(GameSession session, P packet) {
        final Fight fight;
        final PlayerFighter fighter = session.fighter();
        final Spectator spectator = session.spectator();

        if (fighter != null) {
            fight = fighter.fight();
        } else if (spectator != null) {
            fight = spectator.fight();
        } else {
            throw new CloseImmediately("Not in fight");
        }

        fight.execute(() -> {
            try {
                // Use getter instead of local variable because the task is executed
                // asynchronously, so the session may change
                if (session.fighter() != null) {
                    fightingHandler.handle(session, packet);
                } else if (session.spectator() != null) {
                    spectatorHandler.handle(session, packet);
                }
            } catch (Exception e) {
                session.exception(e, packet);
            }
        });
    }

    @Override
    public Class<P> packet() {
        return fightingHandler.packet();
    }
}
