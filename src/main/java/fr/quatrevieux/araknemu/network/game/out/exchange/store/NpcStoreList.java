package fr.quatrevieux.araknemu.network.game.out.exchange.store;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * List of available item on an npc store
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L1043
 */
final public class NpcStoreList {
    final private Collection<ItemTemplate> items;

    final static private ItemEffectsTransformer effectsSerializer = new ItemEffectsTransformer();

    public NpcStoreList(Collection<ItemTemplate> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "EL" +
            items.stream()
                .map(template -> template.id() + ";" + effectsSerializer.serialize(template.effects()))
                .collect(Collectors.joining("|"))
        ;
    }
}
