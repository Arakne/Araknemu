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
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Battlefield;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Movement;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to move near the allies
 */
public final class MoveNearAllies implements ActionGenerator {
    private final Movement movement;

    private List<CoordinateCell<BattlefieldCell>> alliesCells = Collections.emptyList();
    private @Nullable BattlefieldCell currentCell;

    @SuppressWarnings("methodref.receiver.bound")
    public MoveNearAllies() {
        this.movement = new Movement(this::score, scoredCell -> true);
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);
    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        final AIHelper helper = ai.helper();

        if (!helper.canMove() || !helper.allies().stream().findAny().isPresent()) {
            return Optional.empty();
        }

        alliesCells = helper.allies().cells().map(BattlefieldCell::coordinate).collect(Collectors.toList());
        currentCell = ai.fighter().cell();

        return movement.generate(ai, actions);
    }

    /**
     * The score function
     *
     * Select the lowest distance from one ally + lowest average distance
     * If the target cell will block an ally by taking the only free adjacent cell, the score will be lowered
     */
    private double score(CoordinateCell<BattlefieldCell> cell) {
        final BattlefieldMap map = cell.cell().map();

        // Minimum free adjacent cells required to avoid blocking an ally
        // 2 cells should be free to take in account that the selected cell will be blocked after the move
        // In case of the selected cell is the current cell, the cell is already considered as blocked, so only 1 cell should be free
        final int minimumFreeAdjacentCells = cell.cell().equals(currentCell) ? 1 : 2;

        double min = Double.MAX_VALUE;
        double total = 0;
        int count = 0;
        int malus = 0;

        for (CoordinateCell<BattlefieldCell> allyCell : alliesCells) {
            final int distance = cell.distance(allyCell);

            min = Math.min(min, distance);
            total += distance;
            ++count;

            // Selected cell will block the ally because there are not enough free cell around him
            if (distance == 1 && Battlefield.freeAdjacentCellsCount(map, allyCell.cell()) < minimumFreeAdjacentCells) {
                malus += 100;
            }
        }

        return - (min + (total / count)) - malus;
    }
}
