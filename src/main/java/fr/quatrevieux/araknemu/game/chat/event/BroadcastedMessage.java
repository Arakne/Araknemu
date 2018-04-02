package fr.quatrevieux.araknemu.game.chat.event;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Event trigger on broadcasted message sent
 */
final public class BroadcastedMessage {
    final private ChannelType channel;
    final private GamePlayer sender;
    final private String message;
    final private String extra;

    public BroadcastedMessage(ChannelType channel, GamePlayer sender, String message, String extra) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.extra = extra;
    }

    public ChannelType channel() {
        return channel;
    }

    public GamePlayer sender() {
        return sender;
    }

    public String message() {
        return message;
    }

    public String extra() {
        return extra;
    }
}
