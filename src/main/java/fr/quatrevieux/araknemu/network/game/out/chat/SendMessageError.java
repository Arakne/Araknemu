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

package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChatException;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Cannot send the message
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L227
 */
public final class SendMessageError {
    private final ChatException.Error error;
    private final @Nullable String target;

    public SendMessageError(ChatException.Error error, @Nullable String target) {
        this.error = error;
        this.target = target;
    }

    @Override
    public String toString() {
        return "cME" + error.id() + target;
    }
}
