package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * The simulation result of a cast
 */
final public class CastSimulation {
    final private Spell spell;
    final private Fighter caster;
    final private FightCell target;

    private int enemiesLife;
    private int alliesLife;
    private int selfLife;

    private int enemiesBoost;
    private int alliesBoost;
    private int selfBoost;

    public CastSimulation(Spell spell, Fighter caster, FightCell target) {
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
     * Alter life value of the target
     *
     * @param value The life diff. Negative value for damage, positive for heal
     * @param target The target fighter
     */
    public void alterLife(int value, Fighter target) {
        if (value < 0) {
            value = Math.max(value, -target.life().current());
        } else {
            value = Math.min(value, target.life().max() - target.life().current());
        }

        if (target.equals(caster)) {
            selfLife += value;
        } else if (target.team().equals(caster.team())) {
            alliesLife += value;
        } else {
            enemiesLife += value;
        }
    }

    /**
     * Add a damage on the target
     *
     * @param value The damage value. Should be positive
     * @param target The target fighter
     */
    public void addDamage(int value, Fighter target) {
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
    public void addBoost(int value, Fighter target) {
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
    public Fighter caster() {
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
        enemiesLife += (simulation.enemiesLife * percent) / 100;
        alliesLife += (simulation.alliesLife * percent) / 100;
        selfLife += (simulation.selfLife * percent) / 100;

        enemiesBoost += (simulation.enemiesBoost * percent) / 100;
        alliesBoost += (simulation.alliesBoost * percent) / 100;
        selfBoost += (simulation.selfBoost * percent) / 100;
    }
}
