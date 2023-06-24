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

package fr.quatrevieux.araknemu.game.handler.account;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class ListCharactersTest extends GameBaseCase {
    private ListCharacters handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ListCharacters(
            container.get(CharactersService.class)
        );

        login();
        dataSet
            .use(Player.class)
            .use(PlayerItem.class)
        ;
    }

    @Test
    void noCharacters() throws Exception {
        handler.handle(session, new AskCharacterList(false));

        requestStack.assertLast(new CharactersList(ServerList.ONE_YEAR, Collections.EMPTY_LIST));
    }

    @Test
    void withCharacters() throws Exception {
        Player first = dataSet.push(Player.forCreation(1, 2, "first", Race.ECAFLIP, Gender.MALE, new Colors(-1, -1, -1)));
        Player second = dataSet.push(Player.forCreation(1, 2, "second", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(2, 2, "not_my_account", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "not_my_server", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));

        handler.handle(session, new AskCharacterList(false));

        requestStack.assertLast(new CharactersList(ServerList.ONE_YEAR, Arrays.asList(
            new AccountCharacter(
                session.account(),
                first
            ),
            new AccountCharacter(
                session.account(),
                second
            )
        )));
    }
}
