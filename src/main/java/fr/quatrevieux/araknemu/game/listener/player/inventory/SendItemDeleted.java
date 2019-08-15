package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;

/**
 * Send packet for delete item
 */
final public class SendItemDeleted implements Listener<ObjectDeleted> {
    final private GamePlayer player;

    public SendItemDeleted(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ObjectDeleted event) {
        player.send(
            new DestroyItem(event.entry())
        );
    }

    @Override
    public Class<ObjectDeleted> event() {
        return ObjectDeleted.class;
    }
}
