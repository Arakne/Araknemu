package fr.quatrevieux.araknemu.network.game.in.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Change channel subscriptions
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L92
 */
final public class SubscribeChannels implements Packet {
    final static public class Parser implements SinglePacketParser<SubscribeChannels> {
        @Override
        public SubscribeChannels parse(String input) throws ParsePacketException {
            Collection<ChannelType> channels = EnumSet.noneOf(ChannelType.class);

            for (int i = 1; i < input.length(); ++i) {
                channels.add(
                    ChannelType.byChar(input.charAt(i))
                );
            }

            return new SubscribeChannels(
                input.charAt(0) == '+',
                channels
            );
        }

        @Override
        public String code() {
            return "cC";
        }
    }

    final private boolean subscribe;
    final private Collection<ChannelType> channels;

    public SubscribeChannels(boolean subscribe, Collection<ChannelType> channels) {
        this.subscribe = subscribe;
        this.channels = channels;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public Collection<ChannelType> channels() {
        return channels;
    }
}
