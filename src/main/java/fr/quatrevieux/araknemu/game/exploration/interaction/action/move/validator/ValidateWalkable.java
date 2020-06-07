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

import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Validate move path : stop the player when a non-walkable cell is found
 */
final public class ValidateWalkable implements PathValidator {
    @Override
    public Path<ExplorationMapCell> validate(Move move, Path<ExplorationMapCell> path) {
        return path.keepWhile(step -> step.cell().walkable());
    }
}
