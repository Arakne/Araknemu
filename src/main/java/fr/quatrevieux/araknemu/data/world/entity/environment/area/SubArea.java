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

import fr.quatrevieux.araknemu.data.constant.Alignment;

/**
 * Map subareas. Contains alignment
 */
final public class SubArea {
    final private int id;
    final private int area;
    final private String name;
    final private boolean conquestable;
    final private Alignment alignment;

    public SubArea(int id, int area, String name, boolean conquestable, Alignment alignment) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.conquestable = conquestable;
        this.alignment = alignment;
    }

    /**
     * Get the subarea id
     * The id can be found as index of MA.sa object of maps_xx_xxx.swf
     * This is the primary key of the subarea
     */
    public int id() {
        return id;
    }

    /**
     * Get the parent area
     *
     * @see Area#id()
     */
    public int area() {
        return area;
    }

    /**
     * Get the area name
     * Used as human readable label
     */
    public String name() {
        return name;
    }

    /**
     * Does the subarea is conquestable ?
     */
    public boolean conquestable() {
        return conquestable;
    }

    /**
     * The default alignment of the subarea
     * This value is overridden by the prism alignment, if present
     */
    public Alignment alignment() {
        return alignment;
    }
}
