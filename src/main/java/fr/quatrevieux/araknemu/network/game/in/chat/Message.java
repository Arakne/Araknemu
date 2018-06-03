package fr.quatrevieux.araknemu.network.game.in.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Message sent to chat
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L19
 */
final public class Message implements Packet {
    final static public class Parser implements SinglePacketParser<Message> {
        @Override
        public Message parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, "|", 3);

            if (parts.length < 2) {
                throw new ParsePacketException("BM" + input, "Needs at least 2 parts");
            }

            String extra = parts.length == 3 ? parts[2] : "";

            if (parts[0].length() == 1) {
                return new Message(
                    ChannelType.byChar(parts[0].charAt(0)),
                    null,
                    parts[1],
                    extra
                );
            }

            return new Message(
                ChannelType.PRIVATE,
                parts[0],
                parts[1],
                extra
            );
        }

        @Override
        public String code() {
            return "BM";
        }
    }

    final private ChannelType channel;
    final private String target;
    final private String message;
    final private String items;

    public Message(ChannelType channel, String target, String message, String items) {
        this.channel = channel;
        this.target = target;
        this.message = message;
        this.items = items;
    }

    public ChannelType channel() {
        return channel;
    }

    /**
     * The target is null when send to a global chat
     */
    public String target() {
        return target;
    }

    public String message() {
        return message;
    }

    public String items() {
        return items;
    }
}
