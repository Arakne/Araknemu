package fr.quatrevieux.araknemu.game.exploration.exchange.event;

/**
 * The quantity of kamas in the exchange has changed
 */
final public class KamasChanged {
    final private long quantity;

    public KamasChanged(long quantity) {
        this.quantity = quantity;
    }

    /**
     * The new kamas quantity
     */
    public long quantity() {
        return quantity;
    }
}
