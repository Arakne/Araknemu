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

package fr.quatrevieux.araknemu.game.exploration.event;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Trigger when player change its orientation.
 * Dispatch to map
 */
final public class OrientationChanged {
    final private ExplorationPlayer player;
    final private Direction orientation;

    public OrientationChanged(ExplorationPlayer player, Direction orientation) {
        this.player = player;
        this.orientation = orientation;
    }

    public ExplorationPlayer player() {
        return player;
    }

    public Direction orientation() {
        return orientation;
    }
}
