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

package fr.quatrevieux.araknemu.network.game.out.fight.battlefield;

import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;

/**
 * Remove a zone (e.g. glyph or trap) from the battlefield
 *
 * See: https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L989
 */
public final class RemoveZone {
    private final int cell;
    private final int size;
    private final int color;

    public RemoveZone(int cell, int size, int color) {
        this.cell = cell;
        this.size = size;
        this.color = color;
    }

    public RemoveZone(BattlefieldObject object) {
        this(object.cell().id(), object.size(), object.color());
    }

    @Override
    public String toString() {
        return "GDZ-" + cell + ";" + size + ";" + color;
    }
}
