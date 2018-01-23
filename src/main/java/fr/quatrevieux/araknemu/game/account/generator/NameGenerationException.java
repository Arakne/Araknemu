package fr.quatrevieux.araknemu.game.account.generator;

/**
 * Exception raised when name generation occurs
 */
public class NameGenerationException extends Exception {
    public NameGenerationException() {
    }

    public NameGenerationException(String message) {
        super(message);
    }

    public NameGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameGenerationException(Throwable cause) {
        super(cause);
    }

    public NameGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
