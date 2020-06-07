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

package fr.quatrevieux.araknemu.game.player.sprite;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Information and parameters of the player sprite
 */
public interface SpriteInfo {
    /**
     * Get the sprite id
     */
    public int id();

    /**
     * Get the sprite name
     */
    public String name();

    /**
     * Get the sprite colors
     */
    public Colors colors();

    /**
     * Get the sprite gfxId
     */
    public int gfxId();

    /**
     * Get the sprite size
     */
    public SpriteSize size();

    /**
     * Get the sprite accessories
     */
    public Accessories accessories();

    /**
     * Get the player gender
     */
    public Gender gender();

    /**
     * Get the player race
     */
    public Race race();
}
