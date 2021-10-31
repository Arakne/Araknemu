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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Pathfinder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for perform operation on map cells
 *
 * @see AIHelper#cells()
 */
public final class CellsHelper {
    private final AI ai;

    private Decoder<FightCell> decoder;

    CellsHelper(AI ai) {
        this.ai = ai;
    }

    /**
     * Get all available cells
     * A cell is considered available when it's walkable without consider fighters
     *
     * @return Stream of all targetable cells
     */
    public Stream<FightCell> stream() {
        return StreamSupport.stream(ai.map().spliterator(), false).filter(FightCell::walkableIgnoreFighter);
    }

    /**
     * Get cells which is adjacent to the fighter
     * Adjacent cells are cells directly accessible through the four restricted directions (i.e. {@link Direction#restrictedDirections()})
     *
     * @return Stream of cells adjacent to the current cell
     *
     * @see FightersHelper#adjacent() To check adjacent fighters
     */
    public Stream<FightCell> adjacent() {
        return adjacent(ai.fighter().cell());
    }

    /**
     * Get cells which is adjacent to the given cell
     * Adjacent cells are cells directly accessible through the four restricted directions (i.e. {@link Direction#restrictedDirections()})
     *
     * @param cell Cell to check
     *
     * @return Stream of cells adjacent to the given cell
     *
     * @see FightersHelper#adjacent(FightCell) To check adjacent fighters
     */
    public Stream<FightCell> adjacent(FightCell cell) {
        final Decoder<FightCell> decoder = decoder();

        return Arrays.stream(Direction.restrictedDirections())
            .map(direction -> decoder.nextCellByDirection(cell, direction))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(FightCell::walkableIgnoreFighter)
        ;
    }

    /**
     * Create a new instance of the pathfinder
     */
    public Pathfinder<FightCell> pathfinder() {
        return decoder().pathfinder();
    }

    private Decoder<FightCell> decoder() {
        if (decoder == null) {
            decoder = new Decoder<>(ai.map());
        }

        return decoder;
    }
}
