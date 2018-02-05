package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send to client if it's a game master (allow open console)
 */
final public class GMLevel {
    final private boolean gm;

    public GMLevel(boolean gm) {
        this.gm = gm;
    }

    @Override
    public String toString() {
        return "AlK" + (gm ? "1" : "0");
    }
}
