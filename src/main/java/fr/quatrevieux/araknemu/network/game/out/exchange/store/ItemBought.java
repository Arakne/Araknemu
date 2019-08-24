package fr.quatrevieux.araknemu.network.game.out.exchange.store;

/**
 * The item has been bought
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L1148
 */
final public class ItemBought {
    final private boolean success;

    public ItemBought(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "EB" + (success ? "K" : "E");
    }

    /**
     * The item has been successfully bought
     */
    static public ItemBought success() {
        return new ItemBought(true);
    }

    /**
     * Cannot bought the item
     */
    static public ItemBought failed() {
        return new ItemBought(false);
    }
}
