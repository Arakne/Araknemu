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

/**
 * Exception when sending message to chat
 */
public class ChatException extends Exception {
    public enum Error {
        DEFAULT(""),
        UNAUTHORIZED(""),
        SYNTAX_ERROR("S"),
        USER_NOT_CONNECTED("f"),
        USER_NOT_CONNECTED_BUT_TRY_SEND_EXTERNAL("e"),
        USER_NOT_CONNECTED_EXTERNAL_NACK("n");

        final private String id;

        Error(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    final private Error error;

    public ChatException(Error error) {
        this.error = error;
    }

    final public Error error() {
        return error;
    }
}
