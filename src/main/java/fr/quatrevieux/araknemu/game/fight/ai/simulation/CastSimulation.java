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

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * The simulation result of a cast
 *
 * @todo use double instead of int
 */
final public class CastSimulation {
    final private Spell spell;
    final private ActiveFighter caster;
    final private FightCell target;

    private int enemiesLife;
    private int alliesLife;
    private int selfLife;

    private int enemiesBoost;
    private int alliesBoost;
    private int selfBoost;

    private int killedAllies;
    private int killedEnemies;

    public CastSimulation(Spell spell, ActiveFighter caster, FightCell target) {
        this.spell = spell;
        this.caster = caster;
        this.target = target;
    }

    /**
     * The enemies life diff (negative value for damage, positive for heal)
     */
    public int enemiesLife() {
        return enemiesLife;
    }

    /**
     * The allies (without self) life diff (negative value for damage, positive for heal)
     */
    public int alliesLife() {
        return alliesLife;
    }

    /**
     * The self (caster) life diff (negative value for damage, positive for heal)
     */
    public int selfLife() {
        return selfLife;
    }

    /**
     * Number of killed allies
     */
    public int killedAllies() {
        return killedAllies;
    }

    /**
     * Number of killed enemies
     */
    public int killedEnemies() {
        return killedEnemies;
    }

    /**
     * Alter life value of the target
     *
     * @todo handle poison (should not kill)
     * @todo interval instead of value
     *
     * @param value The life diff. Negative value for damage, positive for heal
     * @param target The target fighter
     */
    public void alterLife(int value, PassiveFighter target) {
        // @todo compute chance
        final boolean killed = target.life().current() + value <= 0;

        if (value < 0) {
            value = Math.max(value, -target.life().current());
        } else {
            value = Math.min(value, target.life().max() - target.life().current());
        }

        if (target.equals(caster)) {
            selfLife += value;
        } else if (target.team().equals(caster.team())) {
            alliesLife += value;

            if (killed) {
                ++killedAllies;
            }
        } else {
            enemiesLife += value;

            if (killed) {
                ++killedEnemies;
            }
        }
    }

    /**
     * Add a damage on the target
     *
     * @param value The damage value. Should be positive
     * @param target The target fighter
     */
    public void addDamage(int value, PassiveFighter target) {
        alterLife(-value, target);
    }

    /**
     * The enemy boost value.
     * Negative value for malus, and positive for bonus
     */
    public int enemiesBoost() {
        return enemiesBoost;
    }

    /**
     * The allies boost value (without self).
     * Negative value for malus, and positive for bonus
     */
    public int alliesBoost() {
        return alliesBoost;
    }

    /**
     * The self boost value.
     * Negative value for malus, and positive for bonus
     */
    public int selfBoost() {
        return selfBoost;
    }

    /**
     * Add a boost to the target
     *
     * @param value The boost value. Can be negative for a malus
     * @param target The target fighter
     */
    public void addBoost(int value, PassiveFighter target) {
        if (target.equals(caster)) {
            selfBoost += value;
        } else if (target.team().equals(caster.team())) {
            alliesBoost += value;
        } else {
            enemiesBoost += value;
        }
    }

    /**
     * Get the simulated spell caster
     */
    public ActiveFighter caster() {
        return caster;
    }

    /**
     * Get the simulated spell
     */
    public Spell spell() {
        return spell;
    }

    /**
     * Get the target cell
     */
    public FightCell target() {
        return target;
    }

    /**
     * Merge the simulation result into the current simulation
     *
     * All results will be added considering the percent,
     * which represents the probability of the simulation
     *
     * @param simulation The simulation to merge
     * @param percent The simulation chance int percent. This value as interval of [0, 100]
     */
    public void merge(CastSimulation simulation, int percent) {
        enemiesLife += applyPercent(simulation.enemiesLife, percent);
        alliesLife += applyPercent(simulation.alliesLife, percent);
        selfLife += applyPercent(simulation.selfLife, percent);

        enemiesBoost += applyPercent(simulation.enemiesBoost, percent);
        alliesBoost += applyPercent(simulation.alliesBoost, percent);
        selfBoost += applyPercent(simulation.selfBoost, percent);

        killedAllies += applyPercent(simulation.killedAllies, percent);
        killedEnemies += applyPercent(simulation.killedEnemies, percent);
    }

    private int applyPercent(int value, int percent) {
        if (value == 0) {
            return 0;
        }

        final int withPercent = (value * percent) / 100;

        if (withPercent == 0) {
            return value > 0 ? 1 : -1;
        }

        return withPercent;
    }
}
