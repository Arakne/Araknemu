package fr.quatrevieux.araknemu.data.transformer;

/**
 * The transformer cannot serialize or deserialize
 * This may occurs when data is malformed
 */
public class TransformerException extends IllegalArgumentException {
    public TransformerException() {
    }

    public TransformerException(String s) {
        super(s);
    }

    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformerException(Throwable cause) {
        super(cause);
    }
}
