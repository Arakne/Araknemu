package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Generates actions for the AI
 */
public interface ActionGenerator {
    /**
     * Initialize the action generator when turn starts
     * This method must be called before {@link ActionGenerator#generate(AI)}
     */
    public void initialize(AI ai);

    /**
     * Try to generate an action
     *
     * If the action cannot be generated (not enough points, or not available), an empty optional is returned
     * Return an empty optional will execute the next action on the AI, or stop the turn if there is no valid actions
     */
    public Optional<Action> generate(AI ai);
}
