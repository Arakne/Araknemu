package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.EffectSimulator;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.HashMap;
import java.util.Map;

/**
 * Perform simulation on fight
 */
final public class Simulator {
    final private Map<Integer, EffectSimulator> simulators = new HashMap<>();

    /**
     * Register an effect simulator
     *
     * @param effectId The effect to simulate
     * @param simulator The simulator
     */
    public void register(int effectId, EffectSimulator simulator) {
        simulators.put(effectId, simulator);
    }

    /**
     * Simulate the spell cast
     *
     * @param spell Spell to cast
     * @param caster The caster (current fighter)
     * @param target The cell target
     *
     * @return The simulation result
     */
    public CastSimulation simulate(Spell spell, Fighter caster, FightCell target) {
        CastSimulation normalSimulation = simulate(spell, new CastScope(spell, caster, target).withEffects(spell.effects()));

        if (spell.criticalHit() < 2) {
            return normalSimulation;
        }

        CastSimulation criticalSimulation = simulate(spell, new CastScope(spell, caster, target).withEffects(spell.criticalEffects()));

        int criticalRate = 100 / new BaseCriticalityStrategy(caster).hitRate(spell.criticalHit());

        CastSimulation simulation = new CastSimulation(spell, caster, target);

        simulation.merge(normalSimulation, 100 - criticalRate);
        simulation.merge(criticalSimulation, criticalRate);

        return simulation;
    }

    /**
     * Simulate a cast result
     *
     * @param scope The cast scope
     */
    private CastSimulation simulate(Spell spell, CastScope scope) {
        CastSimulation simulation = new CastSimulation(spell, scope.caster(), scope.target());

        for (CastScope.EffectScope effect : scope.effects()) {
            if (!simulators.containsKey(effect.effect().effect())) {
                continue;
            }

            EffectSimulator simulator = simulators.get(effect.effect().effect());

            if (effect.effect().probability() > 0) {
                CastSimulation probableSimulation = new CastSimulation(spell, scope.caster(), scope.target());
                simulator.simulate(probableSimulation, effect);

                simulation.merge(probableSimulation, effect.effect().probability());
            } else {
                simulator.simulate(simulation, effect);
            }
        }

        return simulation;
    }
}
