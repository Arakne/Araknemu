package fr.quatrevieux.araknemu.network.out;

/**
 * Ping from server
 */
final public class RPing {
    final private String payload;

    public RPing(String payload) {
        this.payload = payload;
    }

    public RPing() {
        this("");
    }

    @Override
    public String toString() {
        return "rping" + payload;
    }
}
