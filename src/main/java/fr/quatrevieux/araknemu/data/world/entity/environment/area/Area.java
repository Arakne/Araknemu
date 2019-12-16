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

package fr.quatrevieux.araknemu.data.world.entity.environment.area;

/**
 * Area data
 * The area contains multiple subarea, and is under a superarea
 */
final public class Area {
    final private int id;
    final private String name;
    final private int superarea;

    public Area(int id, String name, int superarea) {
        this.id = id;
        this.name = name;
        this.superarea = superarea;
    }

    /**
     * The area id
     * This value can be found as index of MA.a into maps_xx_xxx.swf
     * This is the primary key of areas
     */
    public int id() {
        return id;
    }

    /**
     * The area name
     * Used as human readable label
     */
    public String name() {
        return name;
    }

    /**
     * The super area id
     */
    public int superarea() {
        return superarea;
    }
}
