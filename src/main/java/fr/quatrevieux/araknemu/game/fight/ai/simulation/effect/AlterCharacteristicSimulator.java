package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Simulator for simple alter characteristic effect
 */
final public class AlterCharacteristicSimulator implements EffectSimulator {
    final private int multiplier;

    /**
     * Creates without multiplier
     */
    public AlterCharacteristicSimulator() {
        this(1);
    }

    /**
     * Creates with defining a multiplier
     *
     * @param multiplier The value multiplier. Can be negative for malus buff
     */
    public AlterCharacteristicSimulator(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        int value = new EffectValue(effect.effect()).mean().value()
            * multiplier
            * Math.max(effect.effect().duration(), 1)
        ;

        for (Fighter target : effect.targets()) {
            simulation.addBoost(value, target);
        }
    }
}
