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

package fr.quatrevieux.araknemu.data.world.entity.monster;

import fr.arakne.utils.value.Colors;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Map;

/**
 * Template for monster
 * Store characteristics for create real monsters
 */
public final class MonsterTemplate {
    private final int id;
    private final String name;
    private final int gfxId;
    private final Colors colors;
    private final String ai;
    private final Grade[] grades;

    public MonsterTemplate(int id, String name, int gfxId, Colors colors, String ai, Grade[] grades) {
        this.id = id;
        this.name = name;
        this.gfxId = gfxId;
        this.colors = colors;
        this.ai = ai;
        this.grades = grades;
    }

    /**
     * The monster template id (primary key)
     * The value corresponds with `monsters_xx.swf` file, into variable M[id]
     */
    public int id() {
        return id;
    }

    /**
     * The monster name
     * Not used : this is a human readable comment
     */
    public String name() {
        return name;
    }

    /**
     * The monster sprite
     * Must corresponds with file sprites/[id].swf
     */
    public int gfxId() {
        return gfxId;
    }

    /**
     * The monster sprite colors
     */
    public Colors colors() {
        return colors;
    }

    /**
     * The monster AI type to use on fight
     */
    public String ai() {
        return ai;
    }

    /**
     * List of available grades (level, characteristics, spells...)
     */
    @Pure
    public Grade[] grades() {
        return grades;
    }

    public static final class Grade {
        private final @Positive int level;
        private final @Positive int life;
        private final int initiative;
        private final Characteristics characteristics;
        private final Map<Integer, @Positive Integer> spells;

        public Grade(@Positive int level, @Positive int life, int initiative, Characteristics characteristics, Map<Integer, @Positive Integer> spells) {
            this.level = level;
            this.life = life;
            this.initiative = initiative;
            this.characteristics = characteristics;
            this.spells = spells;
        }

        /**
         * The monster level
         * Will be displayed on the sprite (information for the player), but has no more effects
         */
        public @Positive int level() {
            return level;
        }

        /**
         * The monster max life
         */
        public @Positive int life() {
            return life;
        }

        /**
         * The monster initiative
         * Unlike characters, only this value is considered for initiative (i.e. not changed by other stats, or life)
         */
        public int initiative() {
            return initiative;
        }

        /**
         * Get the monster characteristics (including resistances, action and movement points)
         */
        public Characteristics characteristics() {
            return characteristics;
        }

        /**
         * Get the monster spells
         * The response is a map with spell id as key and spell level as value
         */
        public Map<Integer, @Positive Integer> spells() {
            return spells;
        }
    }
}
