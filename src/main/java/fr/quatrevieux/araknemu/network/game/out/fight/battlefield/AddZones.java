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
 * Add a zone (e.g. glyph or trap) on the battlefield
 *
 * See: https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L989
 */
public final class AddZones {
    private final Zone[] zones;

    public AddZones(Zone[] zones) {
        this.zones = zones;
    }

    public AddZones(BattlefieldObject object) {
        this(new Zone[] { new Zone(object.cell().id(), object.size(), object.color()) });
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(3 + 10 * zones.length);

        sb.append("GDZ");
        boolean first = true;

        for (Zone zone : zones) {
            if (first) {
                first = false;
            } else {
                sb.append('|');
            }

            sb
                .append('+')
                .append(zone.cell).append(';')
                .append(zone.size).append(';')
                .append(zone.color)
            ;
        }

        return sb.toString();
    }

    public static final class Zone {
        private final int cell;
        private final int size;
        private final int color;

        public Zone(int cell, int size, int color) {
            this.cell = cell;
            this.size = size;
            this.color = color;
        }
    }
}
