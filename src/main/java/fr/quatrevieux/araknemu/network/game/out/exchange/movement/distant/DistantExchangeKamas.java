package fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant;

/**
 * Change distant kamas quantity on the exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L761
 */
final public class DistantExchangeKamas {
    final private long quantity;

    public DistantExchangeKamas(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "EmKG" + quantity;
    }
}
