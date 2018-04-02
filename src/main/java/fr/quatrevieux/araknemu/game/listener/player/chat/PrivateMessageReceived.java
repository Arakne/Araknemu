package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.chat.event.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.PrivateMessage;

/**
 * Listen private messages
 */
final public class PrivateMessageReceived implements Listener<ConcealedMessage> {
    final private GamePlayer player;

    public PrivateMessageReceived(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ConcealedMessage event) {
        if (player == event.sender()) {
            player.send(
                new PrivateMessage(
                    PrivateMessage.TYPE_TO,
                    event.receiver(),
                    event.message(),
                    event.extra()
                )
            );
        } else if (player == event.receiver()) {
            player.send(
                new PrivateMessage(
                    PrivateMessage.TYPE_FROM,
                    event.sender(),
                    event.message(),
                    event.extra()
                )
            );
        }
    }

    @Override
    public Class<ConcealedMessage> event() {
        return ConcealedMessage.class;
    }
}
