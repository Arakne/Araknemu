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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;

import java.util.Collections;

/**
 * Send all fighters positions
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L193
 */
public final class FighterPositions {
    private final Iterable<? extends FighterData> fighters;

    public FighterPositions(FighterData fighter) {
        this(Collections.singleton(fighter));
    }

    public FighterPositions(Iterable<? extends FighterData> fighters) {
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GIC");

        for (FighterData fighter : fighters) {
            sb.append('|').append(fighter.id()).append(';').append(fighter.cell().id());
        }

        return sb.toString();
    }
}
