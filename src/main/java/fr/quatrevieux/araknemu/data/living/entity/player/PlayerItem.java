package fr.quatrevieux.araknemu.data.living.entity.player;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.util.List;

/**
 * Inventory entry entity
 */
final public class PlayerItem {
    final private int playerId;
    final private int entryId;
    final private int itemTemplateId;
    final private List<ItemTemplateEffectEntry> effects;
    private int quantity;
    private int position;

    public PlayerItem(int playerId, int entryId, int itemTemplateId, List<ItemTemplateEffectEntry> effects, int quantity, int position) {
        this.playerId = playerId;
        this.entryId = entryId;
        this.itemTemplateId = itemTemplateId;
        this.effects = effects;
        this.quantity = quantity;
        this.position = position;
    }

    public int playerId() {
        return playerId;
    }

    public int entryId() {
        return entryId;
    }

    public int itemTemplateId() {
        return itemTemplateId;
    }

    public List<ItemTemplateEffectEntry> effects() {
        return effects;
    }

    public int quantity() {
        return quantity;
    }

    public int position() {
        return position;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
