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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.handler.AbstractFightingPacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.KickFighterRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Kick a fighter during placement state
 *
 * Unlike leave, the kicked fighter will not be punished.
 * Only the team leader can kick a fighter, except himself.
 *
 * @see PlacementState#kick(Fighter)
 */
public final class KickFighter extends AbstractFightingPacketHandler<KickFighterRequest> {
    @Override
    public void handle(GameSession session, Fight fight, PlayerFighter fighter, KickFighterRequest packet) {
        if (fight.active()) {
            throw new ErrorPacket(Error.cantDoDuringFight());
        }

        if (!fighter.isTeamLeader() || packet.fighterId() == fighter.id()) {
            // @todo Im error ?
            throw new ErrorPacket("Cannot kick himself, or if not leader", new Noop());
        }

        boolean success = false;

        for (Fighter teammate : fighter.team().fighters()) {
            if (teammate.id() == packet.fighterId()) {
                success = fight.ifState(PlacementState.class, state -> state.kick(teammate));
                break;
            }
        }

        if (!success) {
            // @todo Im error ?
            session.send(new Noop());
        }
    }

    @Override
    public Class<KickFighterRequest> packet() {
        return KickFighterRequest.class;
    }
}
