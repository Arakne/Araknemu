package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to boost allies (or self)
 *
 * Self boost is priorized to allies boost.
 * The selected spell must, at least, boost allies or self.
 */
final public class Boost implements ActionGenerator, CastSpell.SimulationSelector {
    final private CastSpell generator;

    public Boost(Simulator simulator) {
        this.generator = new CastSpell(simulator, this);
    }

    @Override
    public void initialize(AI ai) {
        generator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        return generator.generate(ai);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        return simulation.alliesBoost() > 0 || simulation.selfBoost() > 0;
    }

    @Override
    public boolean compare(CastSimulation a, CastSimulation b) {
        return score(a) > score(b);
    }

    /**
     * Compute the score for the given simulation
     *
     * @param simulation The simulation result
     *
     * @return The score of the simulation
     */
    private int score(CastSimulation simulation) {
        int score =
            + simulation.alliesBoost()
            + simulation.selfBoost() * 2
            - simulation.enemiesBoost()
        ;

        return score / simulation.spell().apCost();
    }
}
