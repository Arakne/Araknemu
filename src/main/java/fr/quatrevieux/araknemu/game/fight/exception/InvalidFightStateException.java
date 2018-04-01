package fr.quatrevieux.araknemu.game.fight.exception;

import fr.quatrevieux.araknemu.game.fight.state.FightState;

/**
 * Invalid fight state for perform the action
 */
public class InvalidFightStateException extends FightException {
    public InvalidFightStateException(Class<? extends FightState> expected) {
        this(expected, "cannot perform the action");
    }

    public InvalidFightStateException(Class<? extends FightState> expected, String message) {
        super("Invalid fight state, expects " + expected.getSimpleName() + " : " + message);
    }
}
