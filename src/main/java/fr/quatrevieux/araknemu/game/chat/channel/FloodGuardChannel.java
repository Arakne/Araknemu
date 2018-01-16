package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Decorate channel for adding flood guard
 */
final public class FloodGuardChannel implements Channel {
    final private Channel channel;
    final private GameConfiguration.ChatConfiguration configuration;

    final private ConcurrentMap<Integer, Long> lastSentTime = new ConcurrentHashMap<>();

    public FloodGuardChannel(Channel channel, GameConfiguration.ChatConfiguration configuration) {
        this.channel = channel;
        this.configuration = configuration;
    }

    @Override
    public ChannelType type() {
        return channel.type();
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!checkFlood(from)) {
            return;
        }

        channel.send(from, message);
        lastSentTime.put(from.id(), System.currentTimeMillis());
    }

    /**
     * Check for flood timer
     *
     * @param sender The message sender
     *
     * @return true if can sent the message
     */
    private boolean checkFlood(GamePlayer sender) {
        if (configuration.floodTime() < 0) {
            return true;
        }

        if (!lastSentTime.containsKey(sender.id())) {
            return true;
        }

        int remainingTime = (int) ((lastSentTime.get(sender.id()) + configuration.floodTime() * 1000 - System.currentTimeMillis()) / 1000);

        if (remainingTime <= 0) {
            return true;
        }

        sender.send(Information.chatFlood(remainingTime));
        return false;
    }
}
