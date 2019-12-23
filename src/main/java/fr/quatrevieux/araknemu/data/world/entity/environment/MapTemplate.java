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

import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.value.Geoposition;

import java.util.List;

/**
 * Entity for Dofus map
 */
final public class MapTemplate {
    public interface Cell {
        /**
         * Check if the cell do not block the line of sight
         */
        public boolean lineOfSight();

        /**
         * Get the permitted movement type
         *
         * The value is an int in range [0 - 5] :
         *
         * - 0 means not walkable
         * - 1 means walkable, but not on a road
         * - 2 to 5 means different levels of walkable cells. Bigger is the movement, lower is the weight on pathing
         */
        public int movement();

        /**
         * Check if the cell contains an interactive object
         */
        public boolean interactive();

        /**
         * Get the cell object id
         */
        public int objectId();

        /**
         * Check if the cell is active or not
         */
        public boolean active();
    }

    final private int id;
    final private String date;
    final private Dimensions dimensions;
    final private String key;
    final private List<Cell> cells;
    final private List<Integer>[] fightPlaces;
    final private Geoposition geoposition;
    final private int subAreaId;

    public MapTemplate(int id, String date, Dimensions dimensions, String key, List<Cell> cells, List<Integer>[] fightPlaces, Geoposition geoposition, int subAreaId) {
        this.id = id;
        this.date = date;
        this.dimensions = dimensions;
        this.key = key;
        this.cells = cells;
        this.fightPlaces = fightPlaces;
        this.geoposition = geoposition;
        this.subAreaId = subAreaId;
    }

    public int id() {
        return id;
    }

    public String date() {
        return date;
    }

    public Dimensions dimensions() {
        return dimensions;
    }

    public String key() {
        return key;
    }

    public List<Cell> cells() {
        return cells;
    }

    public List<Integer>[] fightPlaces() {
        return fightPlaces;
    }

    /**
     * Get the map coordinates as 2D point
     */
    public Geoposition geoposition() {
        return geoposition;
    }

    /**
     * Get the map subarea
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea#id()
     */
    public int subAreaId() {
        return subAreaId;
    }
}
