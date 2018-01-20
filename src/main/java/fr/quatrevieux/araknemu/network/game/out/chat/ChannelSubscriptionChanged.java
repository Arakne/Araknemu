package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.Collection;

/**
 * Abstract class packet for subscription changed
 */
abstract public class ChannelSubscriptionChanged {
    final private char sign;
    final private Collection<ChannelType> channels;


    public ChannelSubscriptionChanged(char sign, Collection<ChannelType> channels) {
        this.sign = sign;
        this.channels = channels;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("cC");

        sb.append(sign);

        for (ChannelType type : channels) {
            sb.append(type.identifier());
        }

        return sb.toString();
    }
}
