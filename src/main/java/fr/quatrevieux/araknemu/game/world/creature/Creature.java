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

package fr.quatrevieux.araknemu.game.world.creature;

import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;

/**
 * Base interface for Dofus creatures (monsters, players...)
 */
public interface Creature<C extends MapCell> {
    /**
     * Get the sprite
     */
    public Sprite sprite();

    /**
     * Get the creature id
     */
    public int id();

    /**
     * Get the creature cell
     *
     * @throws IllegalStateException When the creature is not yet on a cell (i.e. map change or not fully loaded)
     */
    public C cell();

    /**
     * Get the creature orientation
     */
    public Direction orientation();
}
