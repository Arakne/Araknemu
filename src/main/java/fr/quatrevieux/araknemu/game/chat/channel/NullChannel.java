package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;

/**
 * Null object for channel
 */
final public class NullChannel implements Channel {
    final private ChannelType type;

    public NullChannel(ChannelType type) {
        this.type = type;
    }

    @Override
    public ChannelType type() {
        return type;
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        from.send(new Noop());
    }
}
