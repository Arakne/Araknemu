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

package fr.quatrevieux.araknemu.data.living.entity.environment;

import fr.quatrevieux.araknemu.data.constant.Alignment;

/**
 * Map subareas. Contains alignment
 */
final public class SubArea {
    final private int id;
    final private int area;
    final private String name;
    final private boolean conquestable;

    private Alignment alignment;

    public SubArea(int id, int area, String name, boolean conquestable, Alignment alignment) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.conquestable = conquestable;
        this.alignment = alignment;
    }

    public int id() {
        return id;
    }

    public int area() {
        return area;
    }

    public String name() {
        return name;
    }

    public boolean conquestable() {
        return conquestable;
    }

    public Alignment alignment() {
        return alignment;
    }
}
