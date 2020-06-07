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

package fr.quatrevieux.araknemu.game.exploration.map.event;

import fr.arakne.utils.maps.path.Path;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * A creature is moving on the map
 */
final public class CreatureMoving {
    final private Creature<ExplorationMapCell> creature;
    final private Path<ExplorationMapCell> path;

    public CreatureMoving(Creature<ExplorationMapCell> creature, Path<ExplorationMapCell> path) {
        this.creature = creature;
        this.path = path;
    }

    /**
     * Get the moving creature
     */
    public Creature<ExplorationMapCell> creature() {
        return creature;
    }

    /**
     * Get the move path
     */
    public Path<ExplorationMapCell> path() {
        return path;
    }
}
