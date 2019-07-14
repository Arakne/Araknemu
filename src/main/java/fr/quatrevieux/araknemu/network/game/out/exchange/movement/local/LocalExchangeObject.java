package fr.quatrevieux.araknemu.network.game.out.exchange.movement.local;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Set the object quantity on the local exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L757
 */
final public class LocalExchangeObject {
    final private ItemEntry entry;
    final private int quantity;

    public LocalExchangeObject(ItemEntry entry, int quantity) {
        this.entry = entry;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return quantity > 0
            ? "EMKO+" + entry.id() + "|" + quantity
            : "EMKO-" + entry.id()
        ;
    }
}
