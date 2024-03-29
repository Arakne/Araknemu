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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Initialise the fight interface for a spectator
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L110
 */
public final class JoinFightAsSpectator {
    private final Fight fight;

    public JoinFightAsSpectator(Fight fight) {
        this.fight = fight;
    }

    @Override
    public String toString() {
        return "GJK" + fight.state().id() + "|0|0|1||" + fight.type().id();
    }
}
