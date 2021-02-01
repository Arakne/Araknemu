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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Collection;

/**
 * Send fighters information between two turns
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L326
 */
public final class TurnMiddle {
    private final Collection<Fighter> fighters;

    public TurnMiddle(Collection<Fighter> fighters) {
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GTM");

        for (Fighter fighter : fighters) {
            sb.append('|').append(fighter.id()).append(';');

            if (fighter.dead()) {
                sb.append(1);
                continue;
            }

            sb.append("0;").append(fighter.life().current()).append(';')
                .append(fighter.characteristics().get(Characteristic.ACTION_POINT)).append(';')
                .append(fighter.characteristics().get(Characteristic.MOVEMENT_POINT)).append(';')
                .append(fighter.cell().id()).append(';') // @todo set cell to -1 when hidden
                .append(';') // Not used by client (line 348)
                .append(fighter.life().max())
            ;
        }

        return sb.toString();
    }
}
