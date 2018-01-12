package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Set;

/**
 * List of subscribed chat channels
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L149
 */
final public class ChannelSubscribed {
    final private Set<ChannelType> channels;

    public ChannelSubscribed(Set<ChannelType> channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("cC+");

        for (ChannelType type : channels) {
            sb.append(type.identifier());
        }

        return sb.toString();
    }
}
