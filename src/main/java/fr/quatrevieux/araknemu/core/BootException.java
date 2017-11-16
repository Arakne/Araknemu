package fr.quatrevieux.araknemu.core;

/**
 * Exception raised during booting service
 */
public class BootException extends Exception {
    public BootException() {
    }

    public BootException(String message) {
        super(message);
    }

    public BootException(String message, Throwable cause) {
        super(message, cause);
    }

    public BootException(Throwable cause) {
        super(cause);
    }

    public BootException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
