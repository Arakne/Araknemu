package fr.quatrevieux.araknemu.game.event.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.UpdateItemSet;

/**
 * Send to the client the item set changes
 */
final public class SendItemSetChange implements Listener<EquipmentChanged> {
    final private GamePlayer player;

    public SendItemSetChange(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(EquipmentChanged event) {
        event.entry().item().set().ifPresent(
            itemSet -> player.send(
                new UpdateItemSet(player.inventory().itemSets().get(itemSet))
            )
        );
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
