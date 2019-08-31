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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.world.map.GameMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Map for the fight
 */
final public class FightMap implements GameMap<FightCell>, Iterable<FightCell> {
    final private MapTemplate template;
    final private List<FightCell> cells;

    public FightMap(MapTemplate template) {
        this.template = template;
        this.cells = makeCells(template.cells());
    }

    /**
     * Get the map id
     */
    public int id() {
        return template.id();
    }

    /**
     * Get a fight cell
     *
     * @param cellId The cell id
     */
    @Override
    public FightCell get(int cellId) {
        return cells.get(cellId);
    }

    /**
     * Get start places for a team
     */
    public List<Integer> startPlaces(int team) {
        return template.fightPlaces()[team];
    }

    @Override
    public Dimensions dimensions() {
        return template.dimensions();
    }

    @Override
    public int size() {
        return cells.size();
    }

    @Override
    public Iterator<FightCell> iterator() {
        return cells.iterator();
    }

    /**
     * Clear map data
     */
    public void destroy() {
        for (FightCell cell : cells) {
            cell.fighter().ifPresent(fighter -> cell.removeFighter());
        }

        cells.clear();
    }

    private List<FightCell> makeCells(List<MapTemplate.Cell> template) {
        List<FightCell> cells = new ArrayList<>(template.size());

        for (int i = 0; i < template.size(); ++i) {
            MapTemplate.Cell cell = template.get(i);

            if (!cell.active() || cell.movement() < 2) {
                cells.add(new UnwalkableFightCell(this, template.get(i), i));
            } else {
                cells.add(new WalkableFightCell(this, template.get(i), i));
            }
        }

        return cells;
    }
}
