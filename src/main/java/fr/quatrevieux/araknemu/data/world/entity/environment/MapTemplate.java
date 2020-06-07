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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.world.entity.environment;

import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.data.value.Geolocation;

import java.util.List;

/**
 * Entity for Dofus map
 */
final public class MapTemplate {
    final private int id;
    final private String date;
    final private Dimensions dimensions;
    final private String key;
    final private CellData[] cells;
    final private List<Integer>[] fightPlaces;
    final private Geolocation geolocation;
    final private int subAreaId;
    final private boolean indoor;

    public MapTemplate(int id, String date, Dimensions dimensions, String key, CellData[] cells, List<Integer>[] fightPlaces, Geolocation geolocation, int subAreaId, boolean indoor) {
        this.id = id;
        this.date = date;
        this.dimensions = dimensions;
        this.key = key;
        this.cells = cells;
        this.fightPlaces = fightPlaces;
        this.geolocation = geolocation;
        this.subAreaId = subAreaId;
        this.indoor = indoor;
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

    public CellData[] cells() {
        return cells;
    }

    public List<Integer>[] fightPlaces() {
        return fightPlaces;
    }

    /**
     * Get the map coordinates as 2D point
     *
     * This location can be found into map_xx_xxx.swf, as value of MA.m[mapid]
     */
    public Geolocation geolocation() {
        return geolocation;
    }

    /**
     * Get the map subarea
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea#id()
     */
    public int subAreaId() {
        return subAreaId;
    }

    /**
     * Does the map is an indoor map ?
     * Indoor map are house or underground maps
     */
    public boolean indoor() {
        return indoor;
    }
}
