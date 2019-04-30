package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;

/**
 * Simulate steal life effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler
 */
final public class StealLifeSimulator implements EffectSimulator {
    final private DamageSimulator simulator;

    public StealLifeSimulator(Element element) {
        this.simulator = new DamageSimulator(element);
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        int lastDamage = -simulation.alliesLife() - simulation.enemiesLife();

        // Poison is already handled by the DamageSimulator
        simulator.simulate(simulation, effect);

        int totalDamage = (-simulation.alliesLife() - simulation.enemiesLife()) - lastDamage;

        if (totalDamage > 0) {
            simulation.alterLife(totalDamage / 2, simulation.caster());
        }
    }
}
