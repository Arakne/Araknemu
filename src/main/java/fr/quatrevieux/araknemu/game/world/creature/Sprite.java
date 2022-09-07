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

import fr.arakne.utils.maps.constant.Direction;

/**
 * Interface for representing sprites
 *
 * A sprite is a displayable creature
 */
public interface Sprite {
    public static enum Type {
        PLAYER,
        CREATURE, //?
        MONSTER,
        MONSTER_GROUP,
        NPC,
        OFFLINE_CHARACTER, //seller ?
        COLLECTOR,
        MUTANT,
        MUTANT_PLAYER,
        MOUNT_PARK,
        PRISM;

        /**
         * Get the sprite type id
         * The id is a negative integer
         */
        public int id() {
            return -ordinal();
        }

        /**
         * Convert an entity ID to an unique and universal sprite ID
         * The two last numbers of the ID are the sprite type
         *
         * @param entityId The entity ID to convert
         */
        public int toSpriteId(int entityId) {
            return -(100 * entityId + ordinal());
        }
    }

    /**
     * Get the sprite id
     * This id MUST be unique over all the map
     */
    public int id();

    /**
     * Get the map cell where the sprite is located
     */
    public int cell();

    /**
     * Get the sprite appearance id
     * The appearance will be loaded from `clips/sprites/[id].swf`
     */
    public int gfxId();

    /**
     * Get the sprite orientation
     * Some sprites can only are restricted to 4 directions
     * The playable sprites supports all the 8 directions
     */
    public Direction orientation();

    /**
     * Get the sprite type
     */
    public Type type();

    /**
     * Get the sprite name
     */
    public String name();

    /**
     * Render the sprite string
     */
    public String toString();
}
