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

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.ShowCellRequest;
import fr.quatrevieux.araknemu.network.game.out.fight.CellShown;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Show a cell during a fight
 *
 * Note: this operation is allowed during placement and active fight
 */
public final class ShowCell implements PacketHandler<GameSession, ShowCellRequest> {
    @Override
    public void handle(GameSession session, ShowCellRequest packet) throws Exception {
        final Fighter fighter = NullnessUtil.castNonNull(session.fighter());

        fighter.team().send(new CellShown(fighter, packet.cellId()));
    }

    @Override
    public Class<ShowCellRequest> packet() {
        return ShowCellRequest.class;
    }
}
