package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;

import java.util.stream.Collectors;

/**
 * Send item set data to the client
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L230
 */
final public class UpdateItemSet {
    final static private ItemEffectsTransformer EFFECTS_TRANSFORMER = new ItemEffectsTransformer();

    final private PlayerItemSet itemSet;

    public UpdateItemSet(PlayerItemSet itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public String toString() {
        if (itemSet.isEmpty()) {
            return "OS-" + itemSet.id();
        }

        return
            "OS+" + itemSet.id() + "|" +
            itemSet.items().stream().map(item -> Integer.toString(item.id())).collect(Collectors.joining(";")) + "|" +
            EFFECTS_TRANSFORMER.serialize(itemSet.bonus().effects())
       ;
    }
}
