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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.Optional;

/**
 * Cell for a fight map
 */
public interface FightCell extends MapCell {
    @Override
    public FightMap map();

    /**
     * Check if the cell is walkable, ignoring current fighter
     */
    public boolean walkableIgnoreFighter();

    /**
     * Check if the cell block line of sight
     */
    public boolean sightBlocking();

    /**
     * Get the fighter on the cell
     */
    public Optional<Fighter> fighter();

    /**
     * Set a fighter on this cell
     */
    public void set(Fighter fighter);

    /**
     * Remove the fighter on the cell
     */
    public void removeFighter();
}
