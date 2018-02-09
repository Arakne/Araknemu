package fr.quatrevieux.araknemu.game.world.item.inventory.exception;

/**
 * Exception raised when cannot found the item to the inventory
 */
public class ItemNotFoundException extends InventoryException {
    final private int id;

    public ItemNotFoundException(int id) {
        super("Cannot found the item " + id);

        this.id = id;
    }

    public int id() {
        return id;
    }
}
