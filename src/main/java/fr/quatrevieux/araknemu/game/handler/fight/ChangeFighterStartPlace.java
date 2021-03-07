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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.out.fight.ChangeFighterPlaceError;

/**
 * Change the fighter place before start the fight
 */
public final class ChangeFighterStartPlace implements PacketHandler<GameSession, FighterChangePlace> {
    @Override
    public void handle(GameSession session, FighterChangePlace packet) throws Exception {
        final Fight fight = session.fighter().fight();

        try {
            fight
                .state(PlacementState.class)
                .changePlace(
                    session.fighter(),
                    fight.map().get(packet.cellId())
                )
            ;
        } catch (FightException e) {
            throw new ErrorPacket(new ChangeFighterPlaceError());
        }
    }

    @Override
    public Class<FighterChangePlace> packet() {
        return FighterChangePlace.class;
    }
}
