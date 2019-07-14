package fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

import java.util.List;

/**
 * Set the object quantity on the distant exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L761
 */
final public class DistantExchangeObject {
    final static private Transformer<List<ItemTemplateEffectEntry>> effectsTransformer = new ItemEffectsTransformer();

    final private ItemEntry entry;
    final private int quantity;

    public DistantExchangeObject(ItemEntry entry, int quantity) {
        this.entry = entry;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return quantity > 0
            ? "EmKO+" + entry.id() + "|" + quantity + "|" + entry.templateId() + "|" + effectsTransformer.serialize(entry.effects())
            : "EmKO-" + entry.id()
        ;
    }
}
