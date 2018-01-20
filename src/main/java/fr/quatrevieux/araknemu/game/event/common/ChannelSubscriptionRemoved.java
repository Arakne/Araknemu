package fr.quatrevieux.araknemu.game.event.common;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Collection;
import java.util.Set;

/**
 * Trigger when channels subscription changed
 */
final public class ChannelSubscriptionRemoved {
    final private Collection<ChannelType> channels;

    public ChannelSubscriptionRemoved(Collection<ChannelType> channels) {
        this.channels = channels;
    }

    public Collection<ChannelType> channels() {
        return channels;
    }
}
