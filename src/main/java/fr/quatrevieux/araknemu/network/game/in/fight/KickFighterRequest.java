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

package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu.core.network.parser.Packet;

/**
 * Kick another fighter of the team
 *
 * Note: the packet is same as {@link LeaveFightRequest} but with a fighter id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L27
 */
public final class KickFighterRequest implements Packet {
    private final int fighterId;

    public KickFighterRequest(int fighterId) {
        this.fighterId = fighterId;
    }

    /**
     * The fighter sprite ID to kick
     */
    public int fighterId() {
        return fighterId;
    }
}
