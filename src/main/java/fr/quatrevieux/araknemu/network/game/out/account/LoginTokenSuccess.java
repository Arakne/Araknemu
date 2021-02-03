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

package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Packet for successfully login to game server
 *
 * Parameters :
 * - keyId : If -1 : do not change current key
 *           If 0  : do not use any key (development value)
 *           If [1-16] : Register a new key with race [keyId]
 * - key : The cypher key. Register only if keyId > 0
 *         The key is string encoded with hexadecimal (2 chars per key char)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L735
 */
public final class LoginTokenSuccess {
    private final int keyId;
    private final String key;

    public LoginTokenSuccess(int keyId, String key) {
        this.keyId = keyId;
        this.key = key;
    }

    public LoginTokenSuccess() {
        this(0, "");
    }

    @Override
    public String toString() {
        return "ATK" + Integer.toHexString(keyId).toUpperCase() + key;
    }
}
