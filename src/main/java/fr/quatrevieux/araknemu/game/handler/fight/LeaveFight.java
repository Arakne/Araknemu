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

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.LeavableState;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.LeaveFightRequest;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Leave the fight
 */
public final class LeaveFight implements PacketHandler<GameSession, LeaveFightRequest> {
    @Override
    public void handle(GameSession session, LeaveFightRequest packet) {
        final PlayerFighter fighter = NullnessUtil.castNonNull(session.fighter());

        fighter.fight().state(LeavableState.class).leave(fighter);
    }

    @Override
    public Class<LeaveFightRequest> packet() {
        return LeaveFightRequest.class;
    }
}
