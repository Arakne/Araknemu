package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;

/**
 * Handle a fight state
 */
public interface FightState {
    /**
     * Start the state
     */
    public void start(Fight fight);

    /**
     * Get the state id
     */
    public int id();
}
