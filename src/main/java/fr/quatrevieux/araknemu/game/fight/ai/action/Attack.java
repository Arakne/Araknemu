package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to attack enemies
 *
 * Select spells causing damage on enemies
 * All cells are tested for select the most effective target for each spells
 */
final public class Attack implements ActionGenerator, CastSpell.SimulationSelector {
    final private CastSpell generator;

    public Attack(Simulator simulator) {
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
        return simulation.enemiesLife() < 0;
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
     * @return The score of the simulation. 0 is null
     *
     * @todo Handle the boost value
     */
    private int score(CastSimulation simulation) {
        int score =
            - simulation.enemiesLife()
            + simulation.alliesLife()
            + simulation.selfLife() * 2
        ;

        return score / simulation.spell().apCost();
    }
}
