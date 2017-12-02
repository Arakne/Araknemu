package fr.quatrevieux.araknemu.network.exception;

/**
 * Error which must result of closing session without any messages
 */
public class CloseImmediately extends Exception implements CloseSession {
    public CloseImmediately() {
    }

    public CloseImmediately(String message) {
        super(message);
    }

    public CloseImmediately(String message, Throwable cause) {
        super(message, cause);
    }

    public CloseImmediately(Throwable cause) {
        super(cause);
    }

    public CloseImmediately(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
