package fr.quatrevieux.araknemu.game.chat;

import java.util.HashMap;
import java.util.Map;

/**
 * List of channel types
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/managers/ChatManager.as#L718
 */
public enum ChannelType {
    INFO("i"),
    ERRORS(""), // Error cannot be deactivated
    MESSAGES("*"),
    WISP("#p$"),
    GUILD("%"),
    PVP("!"),
    RECRUITMENT("?"),
    TRADE(":"),
    MEETIC("^"),
    ADMIN("@");

    final static private Map<Character, ChannelType> channels = new HashMap<>();

    static {
        for (ChannelType type : values()) {
            for (char c : type.identifier.toCharArray()) {
                channels.put(c, type);
            }
        }
    }

    final private String identifier;

    ChannelType(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the chat identifier string
     */
    public String identifier() {
        return identifier;
    }

    /**
     * Get a channel by its character
     */
    static public ChannelType byChar(char c) {
        return channels.get(c);
    }
}
