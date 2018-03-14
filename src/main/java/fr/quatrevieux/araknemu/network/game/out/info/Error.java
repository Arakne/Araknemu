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

    public Error(int id, Object... arguments) {
        this(new Entry(id, arguments));
    }
    /**
     * Get the welcome message
     */
    static public Error welcome() {
        return new Error(89);
    }

    /**
     * Cannot do the action on this server
     */
    static public Error cantDoOnServer() {
        return new Error(226);
    }

    /**
     * Cannot learn the spell
     *
     * @param spellId The spell
     */
    static public Error cantLearnSpell(int spellId) {
        return new Error(7, spellId);
    }
}
