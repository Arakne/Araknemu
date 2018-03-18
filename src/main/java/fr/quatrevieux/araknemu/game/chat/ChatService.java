package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.chat.channel.Channel;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddChatChannels;
import fr.quatrevieux.araknemu.game.event.listener.service.RegisterChatListeners;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service for handle chat system
 */
final public class ChatService implements PreloadableService {
    final private ListenerAggregate dispatcher;
    final private GameConfiguration.ChatConfiguration configuration;

    final private Map<ChannelType, Channel> channels = new EnumMap<>(ChannelType.class);

    public ChatService(ListenerAggregate dispatcher, GameConfiguration.ChatConfiguration configuration, Channel[] channels) {
        this.dispatcher = dispatcher;
        this.configuration = configuration;

        for (Channel channel : channels) {
            register(channel);
        }
    }

    /**
     * Register listeners for ChatService
     */
    @Override
    public void preload(Logger logger) {
        logger.info("Initialize chat...");
        dispatcher.add(new RegisterChatListeners());
        dispatcher.add(new AddChatChannels(
            configuration,
            new ChannelsTransformer()
        ));
    }

    /**
     * Resolve channel and send the message
     */
    public void send(GamePlayer sender, Message message) throws ChatException {
        if (!message.items().isEmpty() && !checkItemSyntax(message.message(), message.items())) {
            throw new ChatException(ChatException.Error.SYNTAX_ERROR);
        }

        channels
            .get(message.channel())
            .send(sender, message)
        ;
    }

    private void register(Channel channel) {
        channels.put(channel.type(), channel);
    }

    private boolean checkItemSyntax(String message, String items) {
        String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(items, "!");

        if (parts.length % 2 != 0) {
            return false;
        }

        for (int i = 0; i < parts.length; i += 2) {
            if (!message.contains("°" + (i / 2))) {
                return false;
            }

            if (!StringUtils.isNumeric(parts[i])) {
                return false;
            }

            if (parts[i + 1].isEmpty()) {
                continue;
            }

            int effects = StringUtils.countMatches(parts[i + 1], ',') + 1;

            if (StringUtils.countMatches(parts[i + 1], '#') != 4 * effects) {
                return false;
            }
        }

        return true;
    }
}