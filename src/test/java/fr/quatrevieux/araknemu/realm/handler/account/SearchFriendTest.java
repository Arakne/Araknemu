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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import fr.quatrevieux.araknemu.network.realm.in.FriendSearch;
import fr.quatrevieux.araknemu.network.realm.out.FriendServerList;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class SearchFriendTest extends RealmBaseCase {
    @Test
    void success() throws Exception {
        dataSet.push(new Account(1, "john", "", "john"));
        dataSet.push(Player.forCreation(1, 1, "bob", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "cc", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "dd", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 3, "other", Race.CRA, Gender.FEMALE, new Colors(-1, -1, -1)));

        sendPacket(new FriendSearch("john"));

        requestStack.assertLast(new FriendServerList(Arrays.asList(
            new ServerCharacters(1, 3),
            new ServerCharacters(3, 1)
        )));
    }
}
