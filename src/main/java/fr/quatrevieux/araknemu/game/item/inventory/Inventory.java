package fr.quatrevieux.araknemu.game.item.inventory;

/**
 * Base type for inventory storage (i.e. store items and kamas)
 *
 * @param <E> The item entry type
 */
public interface Inventory<E extends ItemEntry> extends ItemStorage<E>, Wallet {
}
