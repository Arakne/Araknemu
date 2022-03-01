/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

    private static final Map<Character, ChannelType> channels = new HashMap<>();

    static {
        for (ChannelType type : values()) {
            channels.put(type.identifier, type);
        }
    }

    private final char identifier;

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
    public static ChannelType byChar(char c) {
        final ChannelType type =  channels.get(c);

        if (type == null) {
            throw new NoSuchElementException("The channel " + c + " does not exists");
        }

        return type;
    }
}
