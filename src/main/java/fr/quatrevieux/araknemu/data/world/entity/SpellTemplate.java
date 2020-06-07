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

import java.util.List;

/**
 * Template for a spell
 */
final public class SpellTemplate {
    static public class Level {
        final private List<SpellTemplateEffect> effects;
        final private List<SpellTemplateEffect> criticalEffects;
        final private int apCost;
        final private Interval range;
        final private int criticalHit;
        final private int criticalFailure;
        final private boolean lineLaunch;
        final private boolean lineOfSight;
        final private boolean freeCell;
        final private boolean modifiableRange;
        final private int classId;
        final private int launchPerTurn;
        final private int launchPerTarget;
        final private int launchDelay;
        final private List<EffectArea> effectAreas;
        final private int[] requiredStates;
        final private int[] forbiddenStates;
        final private int minPlayerLevel;
        final private boolean endsTurnOnFailure;

        public Level(List<SpellTemplateEffect> effects, List<SpellTemplateEffect> criticalEffects, int apCost, Interval range, int criticalHit, int criticalFailure, boolean lineLaunch, boolean lineOfSight, boolean freeCell, boolean modifiableRange, int classId, int launchPerTurn, int launchPerTarget, int launchDelay, List<EffectArea> effectAreas, int[] requiredStates, int[] forbiddenStates, int minPlayerLevel, boolean endsTurnOnFailure) {
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

        public List<SpellTemplateEffect> effects() {
            return effects;
        }

        public List<SpellTemplateEffect> criticalEffects() {
            return criticalEffects;
        }

        public int apCost() {
            return apCost;
        }

        public Interval range() {
            return range;
        }

        public int criticalHit() {
            return criticalHit;
        }

        public int criticalFailure() {
            return criticalFailure;
        }

        public boolean lineLaunch() {
            return lineLaunch;
        }

        public boolean lineOfSight() {
            return lineOfSight;
        }

        public boolean freeCell() {
            return freeCell;
        }

        public boolean modifiableRange() {
            return modifiableRange;
        }

        public int classId() {
            return classId;
        }

        public int launchPerTurn() {
            return launchPerTurn;
        }

        public int launchPerTarget() {
            return launchPerTarget;
        }

        public int launchDelay() {
            return launchDelay;
        }

        public List<EffectArea> effectAreas() {
            return effectAreas;
        }

        public int[] requiredStates() {
            return requiredStates;
        }

        public int[] forbiddenStates() {
            return forbiddenStates;
        }

        public int minPlayerLevel() {
            return minPlayerLevel;
        }

        public boolean endsTurnOnFailure() {
            return endsTurnOnFailure;
        }
    }

    final private int id;
    final private String name;
    final private int sprite;
    final private String spriteArgs;
    final private Level[] levels;
    final private int[] targets;

    public SpellTemplate(int id, String name, int sprite, String spriteArgs, Level[] levels, int[] targets) {
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

    public Level[] levels() {
        return levels;
    }

    public int[] targets() {
        return targets;
    }
}
