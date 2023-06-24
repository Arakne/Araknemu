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

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharacterAccessories;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharactersListTest extends GameBaseCase {
    @Test
    void generateNoCharacters() {
        assertEquals("ALK123|0", new CharactersList(123, Collections.EMPTY_LIST).toString());
    }

    @Test
    void generateWithOneCharacter() throws ContainerException {
        assertEquals(
            "ALK123|1|5;name;15;90;-1;33;2b;;;1;;;",
            new CharactersList(
                123,
                Collections.singleton(
                    new AccountCharacter(
                        new GameAccount(
                            new Account(12),
                            container.get(AccountService.class),
                            1
                        ),
                        new Player(5, 12, 1, "name", Race.CRA, Gender.MALE, new Colors(-1, 51, 43), 15, null)
                    )
                )
            ).toString()
        );
    }

    @Test
    void generateWithAccessories() throws ContainerException {
        assertEquals(
            "ALK123|1|5;name;15;90;-1;33;2b;,c,17,,;;1;;;",
            new CharactersList(
                123,
                Collections.singleton(
                    new AccountCharacter(
                        new GameAccount(
                            new Account(12),
                            container.get(AccountService.class),
                            1
                        ),
                        new Player(5, 12, 1, "name", Race.CRA, Gender.MALE, new Colors(-1, 51, 43), 15, null),
                        new CharacterAccessories(
                            Arrays.asList(
                                new PlayerItem(1, 1, 12, null, 1, 6),
                                new PlayerItem(1, 1, 23, null, 1, 7)
                            )
                        )
                    )
                )
            ).toString()
        );
    }
}
