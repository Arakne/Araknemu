package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionAdded;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;

/**
 * Send to client the subscribed channels
 */
final public class SubscriptionAddedAcknowledge implements Listener<ChannelSubscriptionAdded> {
    final private GamePlayer player;

    public SubscriptionAddedAcknowledge(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ChannelSubscriptionAdded event) {
        player.send(
            new ChannelSubscribed(event.channels())
        );
    }

    @Override
    public Class<ChannelSubscriptionAdded> event() {
        return ChannelSubscriptionAdded.class;
    }
}
