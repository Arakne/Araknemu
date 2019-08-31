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

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Store the character characteristics
 */
public interface CharacterCharacteristics extends Characteristics {
    /**
     * Get the character base stats (i.e. boosted stats + race stats)
     */
    public MutableCharacteristics base();

    /**
     * Get the total stuff stats
     */
    public Characteristics stuff();

    /**
     * Get the feat (candy ??) stats
     */
    public Characteristics feats();

    /**
     * Get the boost (buff) stats
     */
    public Characteristics boost();

    /**
     * Get the available boost points
     */
    public int boostPoints();

    /**
     * Get the current player initiative
     */
    public int initiative();

    /**
     * Get the current player discernment
     */
    public int discernment();

    /**
     * Get the current player pods
     */
    public int pods();
}
