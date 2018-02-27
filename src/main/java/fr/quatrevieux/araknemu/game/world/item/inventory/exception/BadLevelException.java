package fr.quatrevieux.araknemu.game.world.item.inventory.exception;

/**
 * Raise when an item is to high level for the player
 */
public class BadLevelException extends InventoryException {
    final private int level;

    public BadLevelException(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }
}
