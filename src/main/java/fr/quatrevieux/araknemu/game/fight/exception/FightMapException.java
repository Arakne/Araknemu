package fr.quatrevieux.araknemu.game.fight.exception;

/**
 * Exception for fight map
 */
public class FightMapException extends FightException {
    public FightMapException() {
    }

    public FightMapException(String message) {
        super(message);
    }

    public FightMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public FightMapException(Throwable cause) {
        super(cause);
    }

    public FightMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
