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

/**
 * Send private message to client
 */
final public class PrivateMessage extends AbstractChatMessage {
    final static public char TYPE_FROM = 'F';
    final static public char TYPE_TO   = 'T';

    final private char type;

    public PrivateMessage(char type, GamePlayer target, String message, String extra) {
        super(target, message, extra, false);

        this.type = type;
    }

    @Override
    protected char channel() {
        return type;
    }
}
