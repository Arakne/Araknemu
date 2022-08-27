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
import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Movement;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to move near the allies
 */
public final class MoveNearAllies<F extends ActiveFighter> implements ActionGenerator<F> {
    private final Movement<F> movement;

    private List<CoordinateCell<FightCell>> alliesCells = Collections.emptyList();

    @SuppressWarnings("methodref.receiver.bound")
    public MoveNearAllies() {
        this.movement = new Movement<>(this::score, scoredCell -> true);
    }

    @Override
    public void initialize(AI<F> ai) {
        movement.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, ActionsFactory<F> actions) {
        final AIHelper helper = ai.helper();

        if (!helper.canMove() || !helper.allies().stream().findAny().isPresent()) {
            return Optional.empty();
        }

        alliesCells = helper.allies().cells().map(FightCell::coordinate).collect(Collectors.toList());

        return movement.generate(ai, actions);
    }

    /**
     * The score function
     *
     * Select the lowest distance from one ally + lowest average distance
     * If the target cell will block an ally by taking the only free adjacent cell, the score will be lowered
     */
    private double score(CoordinateCell<FightCell> cell) {
        final BattlefieldMap map = cell.cell().map();

        double min = Double.MAX_VALUE;
        double total = 0;
        int count = 0;
        int malus = 0;

        for (CoordinateCell<FightCell> allyCell : alliesCells) {
            final int distance = cell.distance(allyCell);

            min = Math.min(min, distance);
            total += distance;
            ++count;

            // Selected cell will block the ally because there are not enough free cell around him
            if (distance == 1 && freeAdjacentCellsCount(map, allyCell) < 2) {
                malus += 100;
            }
        }

        return - (min + (total / count)) - malus;
    }

    /**
     * Compute the count of free cells around the given ally cell
     */
    private static int freeAdjacentCellsCount(BattlefieldMap map, CoordinateCell<FightCell> allyCell) {
        int walkableAdjacentCellsCount = 0;

        for (Direction direction : Direction.restrictedDirections()) {
            final int adjacentCellId = allyCell.id() + direction.nextCellIncrement(map.dimensions().width());

            if (adjacentCellId >= 0 && adjacentCellId < map.size() && map.get(adjacentCellId).walkable()) {
                ++walkableAdjacentCellsCount;
            }
        }

        return walkableAdjacentCellsCount;
    }
}
