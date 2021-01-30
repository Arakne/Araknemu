/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.chat;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.channel.Channel;
import fr.quatrevieux.araknemu.game.listener.player.chat.AddChatChannels;
import fr.quatrevieux.araknemu.game.listener.player.chat.InitializeChat;
import fr.quatrevieux.araknemu.game.listener.player.chat.MessageReceived;
import fr.quatrevieux.araknemu.game.listener.player.chat.PrivateMessageReceived;
import fr.quatrevieux.araknemu.game.listener.player.chat.SubscriptionAddedAcknowledge;
import fr.quatrevieux.araknemu.game.listener.player.chat.SubscriptionRemovedAcknowledge;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service for handle chat system
 */
final public class ChatService implements EventsSubscriber {
    final private GameConfiguration.ChatConfiguration configuration;

    final private Map<ChannelType, Channel> channels = new EnumMap<>(ChannelType.class);

    public ChatService(GameConfiguration.ChatConfiguration configuration, Channel[] channels) {
        this.configuration = configuration;

        for (Channel channel : channels) {
            register(channel);
        }
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    final ListenerAggregate dispatcher = event.player().dispatcher();

                    dispatcher.add(new InitializeChat(event.player()));
                    dispatcher.add(new MessageReceived(event.player()));
                    dispatcher.add(new PrivateMessageReceived(event.player()));
                    dispatcher.add(new SubscriptionAddedAcknowledge(event.player()));
                    dispatcher.add(new SubscriptionRemovedAcknowledge(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
            new AddChatChannels(configuration, new ChannelsTransformer()),
        };
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
        final String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(items, "!");

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

            final int effects = StringUtils.countMatches(parts[i + 1], ',') + 1;

            if (StringUtils.countMatches(parts[i + 1], '#') != 4 * effects) {
                return false;
            }
        }

        return true;
    }
}
