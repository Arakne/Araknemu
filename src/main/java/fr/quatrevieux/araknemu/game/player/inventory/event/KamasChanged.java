package fr.quatrevieux.araknemu.game.player.inventory.event;

/**
 * The kamas quantity has changed
 */
final public class KamasChanged {
    final private long lastQuantity;
    final private long newQuantity;

    public KamasChanged(long lastQuantity, long newQuantity) {
        this.lastQuantity = lastQuantity;
        this.newQuantity = newQuantity;
    }

    /**
     * The quantity of kamas before operation
     */
    public long lastQuantity() {
        return lastQuantity;
    }

    /**
     * The new (current) quantity of kamas
     */
    public long newQuantity() {
        return newQuantity;
    }
}
