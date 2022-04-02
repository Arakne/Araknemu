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

package fr.quatrevieux.araknemu.data.world.entity.environment;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Map cell triggers
 * Perform an action when player arrive on the cell
 */
public final class MapTrigger {
    private final int map;
    private final @NonNegative int cell;
    private final int action;
    private final String arguments;
    private final String conditions;

    public MapTrigger(int map, @NonNegative int cell, int action, String arguments, String conditions) {
        this.map = map;
        this.cell = cell;
        this.action = action;
        this.arguments = arguments;
        this.conditions = conditions;
    }

    @Pure
    public int map() {
        return map;
    }

    @Pure
    public @NonNegative int cell() {
        return cell;
    }

    @Pure
    public int action() {
        return action;
    }

    @Pure
    public String arguments() {
        return arguments;
    }

    @Pure
    public String conditions() {
        return conditions;
    }
}
