package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.chat.event.ChannelSubscriptionRemoved;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelUnsubscribed;

/**
 * Send to client the subscribed channels
 */
final public class SubscriptionRemovedAcknowledge implements Listener<ChannelSubscriptionRemoved> {
    final private GamePlayer player;

    public SubscriptionRemovedAcknowledge(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ChannelSubscriptionRemoved event) {
        player.send(
            new ChannelUnsubscribed(event.channels())
        );
    }

    @Override
    public Class<ChannelSubscriptionRemoved> event() {
        return ChannelSubscriptionRemoved.class;
    }
}
