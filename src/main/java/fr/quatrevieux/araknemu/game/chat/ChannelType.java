package fr.quatrevieux.araknemu.game.chat;

import java.util.HashMap;
import java.util.Map;

/**
 * List of channel types
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/managers/ChatManager.as#L718
 */
public enum ChannelType {
    INFO('i'),
    MESSAGES('*'),
    PRIVATE('p'),
    FIGHT_TEAM('#'),
    GROUP('$'),
    GUILD('%'),
    PVP('!'),
    RECRUITMENT('?'),
    TRADE(':'),
    INCARNAM('^'),
    ADMIN('@'),
    MEETIC('¤');

    final static private Map<Character, ChannelType> channels = new HashMap<>();

    static {
        for (ChannelType type : values()) {
            channels.put(type.identifier, type);
        }
    }

    final private char identifier;

    ChannelType(char identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the chat identifier char
     */
    public char identifier() {
        return identifier;
    }

    /**
     * Get a channel by its character
     */
    static public ChannelType byChar(char c) {
        return channels.get(c);
    }
}
