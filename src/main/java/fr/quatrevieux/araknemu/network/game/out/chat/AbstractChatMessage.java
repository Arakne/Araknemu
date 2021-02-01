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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.util.Escape;

/**
 * Base packet for chat message
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L225
 */
public abstract class AbstractChatMessage {
    private final GamePlayer sender;
    private final String message;
    private final String extra;
    private final boolean unescape;

    /**
     * @param sender The message sender
     * @param message The message
     * @param extra Extra info (may be empty)
     * @param unescape Does the message should not be escaped ? Use internally to send HTML chars
     */
    public AbstractChatMessage(GamePlayer sender, String message, String extra, boolean unescape) {
        this.sender = sender;
        this.message = message;
        this.extra = extra;
        this.unescape = unescape;
    }

    @Override
    public final String toString() {
        return "cMK" + channel() + "|" + sender.id() + "|" + escape(sender.name()) + "|" + escape(message) + "|" + escape(extra);
    }

    /**
     * @return The channel identifier
     */
    protected abstract char channel();

    /**
     * Escape HTML chars
     *
     * @param value Value to escape
     *
     * @return Escaped (safe) value
     */
    private String escape(String value) {
        return unescape ? value : Escape.html(value);
    }
}
