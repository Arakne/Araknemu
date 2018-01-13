package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.event.common.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

/**
 * The default chat channel : send to all map
 */
final public class MapChannel implements Channel {
    @Override
    public ChannelType type() {
        return ChannelType.MESSAGES;
    }

    @Override
    public void send(GamePlayer from, Message message) {
        BroadcastedMessage event = new BroadcastedMessage(
            type(),
            from,
            message.message(),
            message.items()
        );

        if (from.isExploring()) {
            from
                .exploration()
                .map()
                .players()
                .forEach(player -> player.dispatch(event))
            ;
        }
    }
}
