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

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class for perform operations on fighters
 *
 * @see AIHelper#enemies()
 * @see AIHelper#allies()
 */
public final class FightersHelper {
    private final AIHelper helper;
    private final AI ai;
    private final Predicate<FighterData> filter;

    FightersHelper(AIHelper helper, AI ai, Predicate<FighterData> filter) {
        this.helper = helper;
        this.ai = ai;
        this.filter = filter;
    }

    /**
     * Get a stream of all alive fighters which match with filter given at constructor
     *
     * @return Stream of fighters
     */
    public Stream<? extends FighterData> stream() {
        return ai.fighters().filter(filter);
    }

    /**
     * Get fighters adjacent to the given cell
     *
     * @param cell Cell to check
     *
     * @return Stream of fighters
     *
     * @see CellsHelper#adjacent(BattlefieldCell)
     */
    @SuppressWarnings("methodref.return") // BattlefieldCell::fighter return a nullable fighter
    public Stream<FighterData> adjacent(BattlefieldCell cell) {
        return helper.cells().adjacent(cell)
            .map(BattlefieldCell::fighter)
            .filter(Objects::nonNull)
            .filter(fighter -> !fighter.hidden())
            .filter(filter)
        ;
    }

    /**
     * Get fighters adjacent to the current cell
     * Note: if there is at lease one adjacent enemy, the current fighter can be tackled
     *
     * @return Stream of fighters
     *
     * @see CellsHelper#adjacent()
     */
    public Stream<FighterData> adjacent() {
        return adjacent(ai.fighter().cell());
    }

    /**
     * Get the nearest fighter
     * If multiple fighters have the same distance, the fighter with lower HP will be returned
     *
     * @return The nearest fighter
     */
    public Optional<? extends FighterData> nearest() {
        final CoordinateCell<BattlefieldCell> currentCell = ai.fighter().cell().coordinate();

        return stream()
            .filter(fighter -> !fighter.hidden())
            .min(Comparator
                .<FighterData>comparingInt(f -> currentCell.distance(f.cell()))
                .thenComparingInt(f -> f.life().current())
            )
        ;
    }

    /**
     * Get cells of all fighters
     *
     * @return Stream of cells
     */
    public Stream<BattlefieldCell> cells() {
        return stream().filter(fighter -> !fighter.hidden()).map(FighterData::cell);
    }

    /**
     * Get all fighters which are in the given range
     *
     * @param range Range to check
     *
     * @return Stream of fighters
     */
    public Stream<? extends FighterData> inRange(Interval range) {
        final CoordinateCell<BattlefieldCell> currentCell = ai.fighter().cell().coordinate();

        return stream().filter(fighter -> !fighter.hidden()).filter(fighter -> range.contains(currentCell.distance(fighter.cell())));
    }

    /**
     * Get the count of fighters which match with the filter given at constructor
     *
     * @return Number of fighters
     */
    public int count() {
        return (int) stream().count();
    }
}
