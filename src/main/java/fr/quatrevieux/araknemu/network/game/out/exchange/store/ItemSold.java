package fr.quatrevieux.araknemu.network.game.out.exchange.store;

/**
 * The item has been sold to the NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L1137
 */
final public class ItemSold {
    final private boolean success;

    public ItemSold(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ES" + (success ? "K" : "E");
    }

    /**
     * Item successfully sold
     */
    static public ItemSold success() {
        return new ItemSold(true);
    }

    /**
     * Fail to sell the item
     */
    static public ItemSold failed() {
        return new ItemSold(false);
    }
}
