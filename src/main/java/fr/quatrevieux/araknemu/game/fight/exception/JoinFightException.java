package fr.quatrevieux.araknemu.game.fight.exception;

import fr.quatrevieux.araknemu.game.fight.JoinFightError;

/**
 * Cannot join the fight
 */
public class JoinFightException extends Exception {
    final private JoinFightError error;

    public JoinFightException(JoinFightError error) {
        this.error = error;
    }

    public JoinFightError error() {
        return error;
    }
}
