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

package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurnList;

import java.util.stream.Collectors;

/**
 * Send the order of fighters turn
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L320
 */
public final class FighterTurnOrder {
    private final FightTurnList list;

    public FighterTurnOrder(FightTurnList list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "GTL|" + list.fighters().stream()
            .map(Fighter::id)
            .map(Object::toString)
            .collect(Collectors.joining("|"))
        ;
    }
}
