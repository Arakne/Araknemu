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

package fr.quatrevieux.araknemu.game.fight.map.util;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;

import java.util.List;

/**
 * Generate placement cells on join fight
 */
public final class PlacementCellsGenerator {
    private final FightMap map;
    private final List<Integer> available;
    private final RandomUtil random;

    private int number = -1;

    /**
     * Construct the generator
     *
     * @param map The fight map
     * @param available List of placement cells
     */
    public PlacementCellsGenerator(FightMap map, List<Integer> available) {
        this(map, available, new RandomUtil());
    }

    private PlacementCellsGenerator(FightMap map, List<Integer> available, RandomUtil random) {
        this.map = map;
        this.available = available;
        this.random = random;
    }

    /**
     * Get the next placement cell
     * The next cell is free and walkable cell
     *
     * If there is no more available start place, a random cell will be taken from the entire map
     */
    public FightCell next() {
        if (number >= available.size() - 1) {
            return randomFightCell();
        }

        return nextAvailableCell();
    }

    /**
     * Returns the next available start cell
     */
    private FightCell nextAvailableCell() {
        final FightCell cell = map.get(available.get(++number));

        if (cell.walkable()) {
            return cell;
        }

        return next();
    }

    /**
     * Get a random cell from the entire map
     */
    private FightCell randomFightCell() {
        for (;;) {
            final FightCell cell = map.get(random.nextInt(map.size()));

            if (cell.walkable()) {
                return cell;
            }
        }
    }

    /**
     * Randomize the available cells for the placement
     *
     * @param map The fight map
     * @param available The available cells
     */
    public static PlacementCellsGenerator randomized(FightMap map, List<Integer> available) {
        final RandomUtil random = new RandomUtil(); // @todo keep static instance ?

        return new PlacementCellsGenerator(map, random.shuffle(available), random);
    }
}
