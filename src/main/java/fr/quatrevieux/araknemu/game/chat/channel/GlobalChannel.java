package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.event.common.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

import java.util.function.Predicate;

/**
 * Channels which send to all online players
 */
final public class GlobalChannel implements Channel {
    final private ChannelType channel;
    final private Predicate<GamePlayer> filter;
    final private PlayerService service;

    public GlobalChannel(ChannelType channel, Predicate<GamePlayer> filter, PlayerService service) {
        this.channel = channel;
        this.filter = filter;
        this.service = service;
    }

    public GlobalChannel(ChannelType channel, PlayerService service) {
        this(channel, player -> true, service);
    }

    @Override
    public ChannelType type() {
        return channel;
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!filter.test(from)) {
            throw new ChatException(ChatException.Error.UNAUTHORIZED);
        }

        BroadcastedMessage event = new BroadcastedMessage(
            type(),
            from,
            message.message(),
            message.items()
        );

        service
            .filter(filter)
            .forEach(player -> player.dispatch(event))
        ;
    }
}
