package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellCaster;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Optional;

/**
 * Try to cast the best spell
 */
final public class CastSpell implements ActionGenerator {
    public interface SimulationSelector {
        /**
         * Check if the simulation is valid
         */
        public boolean valid(CastSimulation simulation);

        /**
         * Compare the two simulation
         * Return true if a is better than b
         *
         * Note: The simulations may be null
         */
        public boolean compare(CastSimulation a, CastSimulation b);
    }

    final private Simulator simulator;
    final private SimulationSelector selector;

    private SpellCaster caster;

    public CastSpell(Simulator simulator, SimulationSelector selector) {
        this.simulator = simulator;
        this.selector = selector;
    }

    @Override
    public void initialize(AI ai) {
        caster = new SpellCaster(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final int actionPoints = ai.turn().points().actionPoints();

        if (actionPoints < 1) {
            return Optional.empty();
        }

        Optional<Fighter> enemy = ai.enemy();

        if (!enemy.isPresent()) {
            return Optional.empty();
        }

        CastSimulation bestSimulation = null;

        for (Spell spell : ai.fighter().spells()) {
            if (spell.apCost() > actionPoints) {
                continue;
            }

            for (FightCell targetCell : ai.fight().map()) {
                // Target or launch is not valid
                if (!targetCell.walkableIgnoreFighter() || !caster.validate(spell, targetCell)) {
                    continue;
                }

                // Simulate spell effects
                CastSimulation simulation = simulator.simulate(spell, ai.fighter(), targetCell);

                // The spell is not valid for the selector
                if (!selector.valid(simulation)) {
                    continue;
                }

                // Select the best simulation
                if (bestSimulation == null || selector.compare(simulation, bestSimulation)) {
                    bestSimulation = simulation;
                }
            }
        }

        return Optional
            .ofNullable(bestSimulation)
            .map(simulation -> caster.create(simulation.spell(), simulation.target()))
        ;
    }
}
