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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.value;

/**
 * Target flags for a spell effect
 */
public final class SpellTarget {
    private final int normal;
    private final int critical;

    public SpellTarget(int normal, int critical) {
        this.normal = normal;
        this.critical = critical;
    }

    /**
     * Target flags for normal spell effect
     */
    public int normal() {
        return normal;
    }

    /**
     * Target flags for critical spell effect
     */
    public int critical() {
        return critical;
    }
}
