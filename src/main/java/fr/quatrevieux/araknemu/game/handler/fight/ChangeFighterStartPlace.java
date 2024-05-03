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
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.handler.AbstractFightingPacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.out.fight.ChangeFighterPlaceError;

/**
 * Change the fighter place before start the fight
 */
public final class ChangeFighterStartPlace extends AbstractFightingPacketHandler<FighterChangePlace> {
    @Override
    public void handle(GameSession session, Fight fight, PlayerFighter fighter, FighterChangePlace packet) throws Exception {
        if (packet.cellId() >= fight.map().size()) {
            throw new ErrorPacket(new ChangeFighterPlaceError());
        }

        final FightCell targetCell = fight.map().get(packet.cellId());

        try {
            fight
                .state(PlacementState.class)
                .changePlace(fighter, targetCell)
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
