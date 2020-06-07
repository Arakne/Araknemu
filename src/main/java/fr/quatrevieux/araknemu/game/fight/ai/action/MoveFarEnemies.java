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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to move far all enemies
 *
 * The selected cell is the cell with the highest distance from the nearest enemy
 * Select only cells with higher distance than current cell
 */
final public class MoveFarEnemies implements ActionGenerator {
    final private Movement movement;

    private List<CoordinateCell<FightCell>> enemiesCells;
    private int currentCellScore = 0;

    public MoveFarEnemies() {
        movement = new Movement(this::score, scoredCell -> scoredCell.score() < currentCellScore);
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final int movementPoints = ai.turn().points().movementPoints();

        if (movementPoints < 1) {
            return Optional.empty();
        }

        enemiesCells = ai.enemies().map(Fighter::cell).map(CoordinateCell::new).collect(Collectors.toList());
        currentCellScore = score(new CoordinateCell<>(ai.fighter().cell()));

        return movement.generate(ai);
    }

    /**
     * The score function
     *
     * Negates the score valid because lowest score is selected first,
     * but we needs that the highest distance is selected first.
     */
    private int score(CoordinateCell<FightCell> cell) {
        return -enemiesCells.stream().mapToInt(cell::distance).min().orElse(0);
    }
}
