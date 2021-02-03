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

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Pathfinder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to move near the selected enemy
 */
public final class MoveNearEnemy implements ActionGenerator {
    private Decoder<FightCell> decoder;
    private Pathfinder<FightCell> pathfinder;
    private ActiveFighter fighter;

    @Override
    public void initialize(AI ai) {
        this.fighter = ai.fighter();
        this.decoder = new Decoder<>(ai.map());
        this.pathfinder = decoder
            .pathfinder()
            .targetDistance(1)
            .walkablePredicate(FightCell::walkableIgnoreFighter)
            .cellWeightFunction(this::cellCost)
        ;
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final int movementPoints = ai.turn().points().movementPoints();

        if (movementPoints < 1) {
            return Optional.empty();
        }

        return ai.enemy()
            .map(enemy -> pathfinder.findPath(ai.fighter().cell(), enemy.cell()).truncate(movementPoints + 1))
            .filter(path -> path.size() > 1)
            .map(path -> ai.turn().actions().move().create(path))
        ;
    }

    /**
     * Compute the cell cost for optimize the path finding
     */
    private int cellCost(FightCell cell) {
        // A fighter is on the cell : the cell is not walkable
        // But the fighter may leave the place at the next turn
        // The cost is higher than a simple detour, but permit to resolve a path blocked by a fighter
        if (cell.fighter().isPresent()) {
            return 15;
        }

        int cost = 1;

        // @todo check agility for tackle ?
        for (Direction direction : Direction.restrictedDirections()) {
            if (decoder.nextCellByDirection(cell, direction)
                .flatMap(FightCell::fighter)
                .filter(other -> !other.team().equals(fighter.team()))
                .isPresent()
            ) {
                // Add a cost of 3 for each enemy around the cell
                // This cost corresponds to the detour cost + 1
                cost += 3;
            }
        }

        return cost;
    }
}
