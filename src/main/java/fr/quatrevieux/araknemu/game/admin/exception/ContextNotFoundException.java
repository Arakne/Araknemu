package fr.quatrevieux.araknemu.game.admin.exception;

/**
 * Raise when a context cannot be found
 */
public class ContextNotFoundException extends ContextException {
    final private String context;

    public ContextNotFoundException(String context) {
        this.context = context;
    }

    public String context() {
        return context;
    }
}
