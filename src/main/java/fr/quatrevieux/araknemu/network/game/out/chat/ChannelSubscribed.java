package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Collection;

/**
 * List of subscribed chat channels
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L149
 */
final public class ChannelSubscribed extends ChannelSubscriptionChanged {
    public ChannelSubscribed(Collection<ChannelType> channels) {
        super('+', channels);
    }
}
