package fr.quatrevieux.araknemu.game.fight.exception;

/**
 * Base exception for fight
 */
public class FightException extends RuntimeException {
    public FightException() {
    }

    public FightException(String message) {
        super(message);
    }

    public FightException(String message, Throwable cause) {
        super(message, cause);
    }

    public FightException(Throwable cause) {
        super(cause);
    }

    public FightException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
