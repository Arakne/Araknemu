package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.AddItem;

/**
 * Send item data for added object
 */
final public class SendItemData implements Listener<ObjectAdded> {
    final private GamePlayer player;

    public SendItemData(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ObjectAdded event) {
        player.send(
            new AddItem(event.entry())
        );
    }

    @Override
    public Class<ObjectAdded> event() {
        return ObjectAdded.class;
    }
}
