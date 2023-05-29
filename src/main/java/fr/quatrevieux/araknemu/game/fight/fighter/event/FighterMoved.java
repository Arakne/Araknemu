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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

/**
 * A fighter has moved
 * This event is dispatched by {@link Fighter#move(FightCell)}
 */
public final class FighterMoved {
    private final Fighter fighter;
    private final FightCell cell;

    public FighterMoved(Fighter fighter, FightCell cell) {
        this.fighter = fighter;
        this.cell = cell;
    }

    /**
     * @return The moved fighter
     */
    public Fighter fighter() {
        return fighter;
    }

    /**
     * @return The new cell
     */
    public FightCell cell() {
        return cell;
    }
}
