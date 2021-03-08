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

package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;

/**
 * SubArea for exploration map
 */
public final class ExplorationSubArea {
    private final SubArea subArea;
    private final Area area;

    public ExplorationSubArea(SubArea subArea, Area area) {
        this.subArea = subArea;
        this.area = area;
    }

    /**
     * Get the id of the subarea
     *
     * @see SubArea#id()
     */
    public int id() {
        return subArea.id();
    }

    /**
     * Get the parent area
     */
    public Area area() {
        return area;
    }

    /**
     * Get the subarea alignment
     * The alignment can be overridden by a prism
     */
    public Alignment alignment() {
        return subArea.alignment();
    }
}
