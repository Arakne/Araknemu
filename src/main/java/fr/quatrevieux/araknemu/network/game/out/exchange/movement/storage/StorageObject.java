package fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

import java.util.List;

/**
 * Set the object quantity on the storage
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L1072
 */
final public class StorageObject {
    final static private Transformer<List<ItemTemplateEffectEntry>> effectsTransformer = new ItemEffectsTransformer();

    final private ItemEntry entry;

    public StorageObject(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return entry.quantity() > 0
            ? "EsKO+" + entry.id() + "|" + entry.quantity() + "|" + entry.templateId() + "|" + effectsTransformer.serialize(entry.effects())
            : "EsKO-" + entry.id()
        ;
    }
}
