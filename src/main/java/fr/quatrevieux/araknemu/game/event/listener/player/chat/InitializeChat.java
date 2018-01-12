package fr.quatrevieux.araknemu.game.event.listener.player.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.GameJoined;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.ChannelSubscribed;

import java.util.EnumSet;

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
            new ChannelSubscribed(
                EnumSet.allOf(ChannelType.class) // @todo load channel from player
            )
        );
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
