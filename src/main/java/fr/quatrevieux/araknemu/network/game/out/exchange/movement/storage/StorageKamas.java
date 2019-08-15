package fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage;

/**
 * Change storage kamas quantity
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L1078
 */
final public class StorageKamas {
    final private long quantity;

    public StorageKamas(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "EsKG" + quantity;
    }
}
