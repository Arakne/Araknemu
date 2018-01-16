package fr.quatrevieux.araknemu.network.game.out.info;

/**
 * Information messages
 */
final public class Information extends InformationMessage {
    public Information(Entry... entries) {
        super(Type.INFO, entries);
    }

    public Information(int id) {
        this(new Entry(id));
    }

    public Information(int id, Object... arguments) {
        this(new Entry(id, arguments));
    }

    /**
     * Message for global channel flood
     *
     * @param remainingSeconds Remaining time in seconds before send another message
     */
    static public Information chatFlood(int remainingSeconds) {
        return new Information(115, remainingSeconds);
    }
}
