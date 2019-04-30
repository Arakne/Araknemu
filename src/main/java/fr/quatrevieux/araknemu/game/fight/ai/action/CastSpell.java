package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
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

    private SpellConstraintsValidator validator;

    public CastSpell(Simulator simulator, SimulationSelector selector) {
        this.simulator = simulator;
        this.selector = selector;
    }

    @Override
    public void initialize(AI ai) {
        validator = new SpellConstraintsValidator(ai.turn());
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

            for (int cellId = 0; cellId < ai.fight().map().size(); ++cellId) {
                FightCell targetCell = ai.fight().map().get(cellId);

                // Target or launch is not valid
                if (!targetCell.walkableIgnoreFighter() || validator.validate(spell, targetCell) != null) {
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

        // @todo cast factory
        return Optional
            .ofNullable(bestSimulation)
            .map(simulation -> new Cast(ai.turn(), ai.fighter(), simulation.spell(), simulation.target()))
        ;
    }
}
