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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Validate restricted directions if the player is not allowed
 */
final public class ValidateRestrictedDirections implements PathValidator {
    @Override
    public Path<ExplorationMapCell> validate(Move move, Path<ExplorationMapCell> path) {
        if (move.performer().player().restrictions().canMoveAllDirections()) {
            return path;
        }

        // Validate directions expect the first one : its value is always "a"
        if (path.stream().skip(1).map(PathStep::direction).allMatch(Direction::restricted)) {
            return path;
        }

        throw new IllegalArgumentException("Invalid direction detected");
    }
}
