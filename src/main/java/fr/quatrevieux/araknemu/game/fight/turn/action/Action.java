package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.time.Duration;

/**
 * Action for a fight turn
 */
public interface Action {
    /**
     * Validate the action before start
     */
    public boolean validate();

    /**
     * Start to perform the action
     */
    public ActionResult start();

    /**
     * Get the action performer
     */
    public Fighter performer();

    /**
     * Get the action type
     */
    public ActionType type();

    /**
     * End the action normally (i.e. the action is successfully done)
     */
    public void end();

    /**
     * Get the maximum action duration (will invoke end when this duration is reached)
     */
    public Duration duration();
}
