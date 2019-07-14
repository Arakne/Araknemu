package fr.quatrevieux.araknemu.network.game.out.exchange.movement.local;

/**
 * Change local kamas quantity on the exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L757
 */
final public class LocalExchangeKamas {
    final private long quantity;

    public LocalExchangeKamas(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "EMKG" + quantity;
    }
}
