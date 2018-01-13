package fr.quatrevieux.araknemu.game.event.listener.player.chat;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;

/**
 * Listen broadcasted messages
 *
 * @todo filter
 */
final public class MessageReceived implements Listener<BroadcastedMessage> {
    final private GamePlayer player;

    public MessageReceived(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(BroadcastedMessage event) {
        player.send(
            new MessageSent(
                event.sender(),
                event.channel(),
                event.message(),
                event.extra()
            )
        );
    }

    @Override
    public Class<BroadcastedMessage> event() {
        return BroadcastedMessage.class;
    }
}
