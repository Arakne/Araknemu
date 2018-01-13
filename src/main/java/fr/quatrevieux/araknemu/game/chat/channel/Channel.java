package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

/**
 * Chat channel
 */
public interface Channel {
    /**
     * Get the channel type
     */
    public ChannelType type();

    /**
     * Send a message to the channel
     *
     * @param from The sender
     * @param message The message
     */
    public void send(GamePlayer from, Message message);
}
