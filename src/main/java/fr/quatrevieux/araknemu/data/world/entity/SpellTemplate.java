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

package fr.quatrevieux.araknemu.data.world.entity;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Template for a spell
 */
public final class SpellTemplate {
    private final int id;
    private final String name;
    private final int sprite;
    private final String spriteArgs;
    private final @Nullable Level[] levels;
    private final int[] targets;

    public SpellTemplate(int id, String name, int sprite, String spriteArgs, @Nullable Level[] levels, int[] targets) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.spriteArgs = spriteArgs;
        this.levels = levels;
        this.targets = targets;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int sprite() {
        return sprite;
    }

    public String spriteArgs() {
        return spriteArgs;
    }

    public @Nullable Level[] levels() {
        return levels;
    }

    public int[] targets() {
        return targets;
    }

    public static final class Level {
        private final List<SpellTemplateEffect> effects;
        private final List<SpellTemplateEffect> criticalEffects;
        private final @NonNegative int apCost;
        private final Interval range;
        private final @NonNegative int criticalHit;
        private final @NonNegative int criticalFailure;
        private final boolean lineLaunch;
        private final boolean lineOfSight;
        private final boolean freeCell;
        private final boolean modifiableRange;
        private final int classId;
        private final int launchPerTurn;
        private final int launchPerTarget;
        private final int launchDelay;
        private final List<EffectArea> effectAreas;
        private final int[] requiredStates;
        private final int[] forbiddenStates;
        private final int minPlayerLevel;
        private final boolean endsTurnOnFailure;

        public Level(List<SpellTemplateEffect> effects, List<SpellTemplateEffect> criticalEffects, @NonNegative int apCost, Interval range, @NonNegative int criticalHit, @NonNegative int criticalFailure, boolean lineLaunch, boolean lineOfSight, boolean freeCell, boolean modifiableRange, int classId, int launchPerTurn, int launchPerTarget, int launchDelay, List<EffectArea> effectAreas, int[] requiredStates, int[] forbiddenStates, int minPlayerLevel, boolean endsTurnOnFailure) {
            this.effects = effects;
            this.criticalEffects = criticalEffects;
            this.apCost = apCost;
            this.range = range;
            this.criticalHit = criticalHit;
            this.criticalFailure = criticalFailure;
            this.lineLaunch = lineLaunch;
            this.lineOfSight = lineOfSight;
            this.freeCell = freeCell;
            this.modifiableRange = modifiableRange;
            this.classId = classId;
            this.launchPerTurn = launchPerTurn;
            this.launchPerTarget = launchPerTarget;
            this.launchDelay = launchDelay;
            this.effectAreas = effectAreas;
            this.requiredStates = requiredStates;
            this.forbiddenStates = forbiddenStates;
            this.minPlayerLevel = minPlayerLevel;
            this.endsTurnOnFailure = endsTurnOnFailure;
        }

        /**
         * List of normal effects
         */
        public List<SpellTemplateEffect> effects() {
            return effects;
        }

        /**
         * List of critical effects
         */
        public List<SpellTemplateEffect> criticalEffects() {
            return criticalEffects;
        }

        /**
         * AP cost for launch the spell
         */
        public @NonNegative int apCost() {
            return apCost;
        }

        /**
         * The cast range
         */
        public Interval range() {
            return range;
        }

        /**
         * Percent of chance to get a critical hit
         *
         * @see fr.quatrevieux.araknemu.game.fight.castable.Castable#criticalHit()
         */
        public @NonNegative int criticalHit() {
            return criticalHit;
        }

        /**
         * Percent of chance to get a critical failure
         *
         * @see fr.quatrevieux.araknemu.game.fight.castable.Castable#criticalFailure()
         */
        public @NonNegative int criticalFailure() {
            return criticalFailure;
        }

        /**
         * Force spell to be launched in line
         */
        public boolean lineLaunch() {
            return lineLaunch;
        }

        /**
         * Launch blocked by line of sight (cannot go through obstacle)
         */
        public boolean lineOfSight() {
            return lineOfSight;
        }

        /**
         * Requires a free cell (i.e. without fighter) to be launched
         */
        public boolean freeCell() {
            return freeCell;
        }

        /**
         * The spell range can be modified with buff or items
         */
        public boolean modifiableRange() {
            return modifiableRange;
        }

        /**
         * The character class id
         */
        public int classId() {
            return classId;
        }

        /**
         * Maximum launch count per turn
         * Zero for no limit
         */
        public int launchPerTurn() {
            return launchPerTurn;
        }

        /**
         * Maximum launch count per target (i.e. fighter)
         * Zero for no limit
         */
        public int launchPerTarget() {
            return launchPerTarget;
        }

        /**
         * Number of turns between two launch
         * Zero for ignore le delay
         */
        public int launchDelay() {
            return launchDelay;
        }

        /**
         * Get the area per spell effect
         * The areas are indexed by the effect index (i.e. first effect (index=0) as the first area (index=0))
         */
        public List<EffectArea> effectAreas() {
            return effectAreas;
        }

        /**
         * Get ids of required stats to launch the spell
         */
        public int[] requiredStates() {
            return requiredStates;
        }

        /**
         * Get ids of forbidden stats to launch the spell
         */
        public int[] forbiddenStates() {
            return forbiddenStates;
        }

        /**
         * Get the minimal player level (inclusive) for use the spell
         */
        public int minPlayerLevel() {
            return minPlayerLevel;
        }

        /**
         * Critical hit will end the current fighter turn
         */
        public boolean endsTurnOnFailure() {
            return endsTurnOnFailure;
        }
    }
}
