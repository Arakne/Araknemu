package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Serialize item entry
 */
final public class ItemSerializer {
    final private ItemEntry entry;

    final static private ItemEffectsTransformer EFFECTS_TRANSFORMER = new ItemEffectsTransformer();

    public ItemSerializer(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return Integer.toHexString(entry.id()) + "~"
            + Integer.toHexString(entry.templateId()) + "~"
            + Integer.toHexString(entry.quantity()) + "~"
            + (entry.isDefaultPosition() ? "" : Integer.toHexString(entry.position())) + "~"
            + EFFECTS_TRANSFORMER.serialize(entry.effects());
    }
}
