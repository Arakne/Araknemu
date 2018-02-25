package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectMoved;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Entry for player repository
 */
final public class InventoryEntry implements ItemEntry {
    final private PlayerInventory inventory;
    final private PlayerItem entity;
    final private Item item;

    public InventoryEntry(PlayerInventory inventory, PlayerItem entity, Item item) {
        this.inventory = inventory;
        this.entity = entity;
        this.item = item;
    }

    @Override
    public int id() {
        return entity.entryId();
    }

    @Override
    public int position() {
        return entity.position();
    }

    @Override
    public Item item() {
        return item;
    }

    @Override
    public int quantity() {
        return entity.quantity();
    }

    @Override
    public List<ItemTemplateEffectEntry> effects() {
        return entity.effects();
    }

    @Override
    public int templateId() {
        return entity.itemTemplateId();
    }

    @Override
    public void add(int quantity) {
        changeQuantity(quantity() + quantity);
    }

    @Override
    public void remove(int quantity) throws InventoryException {
        if (quantity > quantity() || quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        if (quantity == quantity()) {
            entity.setQuantity(0);
            inventory.delete(this);
            return;
        }

        changeQuantity(quantity() - quantity);
    }

    /**
     * Move the entry to a new position
     *
     * @param position The new position
     * @param quantity Quantity to move
     */
    public void move(int position, int quantity) throws InventoryException {
        if (quantity > quantity() || quantity <= 0) {
            throw new InventoryException("Invalid quantity given");
        }

        if (quantity == quantity()) {
            if (inventory.move(this, position)) {
                changePosition(position);
            }

            return;
        }

        inventory.add(item, quantity, position);
        remove(quantity);
    }

    /**
     * Get the database entity
     */
    public PlayerItem entity() {
        return entity;
    }

    private void changeQuantity(int quantity) {
        entity.setQuantity(quantity);
        inventory.dispatch(new ObjectQuantityChanged(this));
    }

    private void changePosition(int position) {
        entity.setPosition(position);
        inventory.dispatch(new ObjectMoved(this));
    }

    /**
     * Create a new entry
     */
    static InventoryEntry create(PlayerInventory inventory, int id, Item item, int quantity, int position) {
        return new InventoryEntry(
            inventory,
            new PlayerItem(
                inventory.playerId(),
                id,
                item.template().id(),
                item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()),
                quantity,
                position
            ),
            item
        );
    }
}
