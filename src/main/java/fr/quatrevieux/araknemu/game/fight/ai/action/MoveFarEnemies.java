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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to move far all enemies
 *
 * The selected cell is the cell with the highest distance from the nearest enemy
 * Select only cells with higher distance than current cell
 */
public final class MoveFarEnemies<F extends ActiveFighter> implements ActionGenerator<F> {
    private final Movement<F> movement;

    private List<CoordinateCell<BattlefieldCell>> enemiesCells = Collections.emptyList();

    @SuppressWarnings("methodref.receiver.bound")
    public MoveFarEnemies() {
        movement = new Movement<>(this::score, scoredCell -> true);
    }

    @Override
    public void initialize(AI<F> ai) {
        movement.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        final AIHelper helper = ai.helper();

        if (!helper.canMove()) {
            return Optional.empty();
        }

        enemiesCells = helper.enemies().cells().map(BattlefieldCell::coordinate).collect(Collectors.toList());

        return movement.generate(ai, actions);
    }

    /**
     * The score function
     *
     * Select the highest distance
     */
    private double score(CoordinateCell<BattlefieldCell> cell) {
        return NullnessUtil.castNonNull(enemiesCells).stream().mapToDouble(cell::distance).min().orElse(0);
    }
}
