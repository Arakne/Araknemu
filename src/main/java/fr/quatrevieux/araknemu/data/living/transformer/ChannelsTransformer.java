package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.game.chat.ChannelType;

import java.util.EnumSet;
import java.util.Set;

/**
 * Transformer for subscribed channels
 */
final public class ChannelsTransformer implements Transformer<Set<ChannelType>> {
    @Override
    public String serialize(Set<ChannelType> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ChannelType channel : value) {
            sb.append(channel.identifier());
        }

        return sb.toString();
    }

    @Override
    public Set<ChannelType> unserialize(String serialize) {
        Set<ChannelType> channels = EnumSet.noneOf(ChannelType.class);

        if (serialize == null || serialize.isEmpty()) {
            return channels;
        }

        for (int i = 0; i < serialize.length(); ++i) {
            channels.add(
                ChannelType.byChar(serialize.charAt(i))
            );
        }

        return channels;
    }
}
