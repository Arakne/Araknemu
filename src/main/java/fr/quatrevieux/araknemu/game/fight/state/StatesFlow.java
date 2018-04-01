package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Handle fight states flow
 */
final public class StatesFlow {
    final private FightState[] states;
    private int current = 0;

    public StatesFlow(FightState... states) {
        this.states = states;
    }

    /**
     * Get the current fight state
     */
    public FightState current() {
        return states[current];
    }

    /**
     * Start the next state
     */
    public void next(Fight fight) {
        states[++current].start(fight);
    }
}
