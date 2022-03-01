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
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Movement;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to move near the allies
 */
public final class MoveNearAllies implements ActionGenerator {
    private final Movement movement;

    private List<CoordinateCell<FightCell>> alliesCells = Collections.emptyList();

    @SuppressWarnings("methodref.receiver.bound")
    public MoveNearAllies() {
        this.movement = new Movement(this::score, scoredCell -> true);
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final AIHelper helper = ai.helper();

        if (!helper.canMove() || !helper.allies().stream().findAny().isPresent()) {
            return Optional.empty();
        }

        alliesCells = helper.allies().cells().map(FightCell::coordinate).collect(Collectors.toList());

        return movement.generate(ai);
    }

    /**
     * The score function
     *
     * Select the lowest distance from one ally + lowest average distance
     */
    private double score(CoordinateCell<FightCell> cell) {
        double min = Double.MAX_VALUE;
        double total = 0;
        int count = 0;

        for (CoordinateCell<FightCell> allyCell : alliesCells) {
            final double distance = cell.distance(allyCell);

            min = Math.min(min, distance);
            total += distance;
            ++count;
        }

        return -(min + (total / count));
    }
}
