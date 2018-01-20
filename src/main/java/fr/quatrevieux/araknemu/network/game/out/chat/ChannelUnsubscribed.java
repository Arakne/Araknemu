package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Collection;

/**
 * Remove chat channel subscriptions
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L149
 */
final public class ChannelUnsubscribed extends ChannelSubscriptionChanged {
    public ChannelUnsubscribed(Collection<ChannelType> channels) {
        super('-', channels);
    }
}
