package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;

/**
 * Initialize the chat on join game
 */
final public class InitializeChat implements Listener<GameJoined> {
    final private GamePlayer player;

    public InitializeChat(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(GameJoined event) {
        player.send(
            new ChannelSubscribed(player.subscriptions())
        );
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
