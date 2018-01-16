package fr.quatrevieux.araknemu.network.game.out.info;

/**
 * Error message
 */
final public class Error extends InformationMessage {
    public Error(Entry... entries) {
        super(Type.ERROR, entries);
    }

    public Error(int id) {
        this(new Entry(id));
    }

    /**
     * Get the welcome message
     */
    static public Error welcome() {
        return new Error(89);
    }
}
