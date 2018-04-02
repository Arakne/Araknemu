package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;

/**
 * Send the new item quantity
 */
final public class SendItemQuantity implements Listener<ObjectQuantityChanged> {
    final private GamePlayer player;

    public SendItemQuantity(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ObjectQuantityChanged event) {
        player.send(
            new ItemQuantity(event.entry())
        );
    }

    @Override
    public Class<ObjectQuantityChanged> event() {
        return ObjectQuantityChanged.class;
    }
}
