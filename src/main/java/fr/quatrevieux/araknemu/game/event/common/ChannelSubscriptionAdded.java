package fr.quatrevieux.araknemu.game.event.common;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Collection;

/**
 * Trigger when channels subscription changed
 */
final public class ChannelSubscriptionAdded {
    final private Collection<ChannelType> channels;

    public ChannelSubscriptionAdded(Collection<ChannelType> channels) {
        this.channels = channels;
    }

    public Collection<ChannelType> channels() {
        return channels;
    }
}
