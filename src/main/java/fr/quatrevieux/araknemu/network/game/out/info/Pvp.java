package fr.quatrevieux.araknemu.network.game.out.info;

/**
 * Pvp messages
 */
final public class Pvp extends InformationMessage {
    public Pvp(Entry... entries) {
        super(Type.PVP, entries);
    }

    public Pvp(int id) {
        this(new Entry(id));
    }
}
