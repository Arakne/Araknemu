package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Send message to client
 *
 * @todo Escape message
 */
final public class MessageSent {
    final private GamePlayer sender;
    final private ChannelType channel;
    final private String message;
    final private String extra;

    public MessageSent(GamePlayer sender, ChannelType channel, String message, String extra) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "cMK" + channel.identifier() + "|" + sender.id() + "|" + sender.name() + "|" + message + "|" + extra;
    }
}
