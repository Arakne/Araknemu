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
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreated;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreationError;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CreateCharacterTest extends GameBaseCase {
    private CreateCharacter handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CreateCharacter(
            container.get(CharactersService.class)
        );

        dataSet
            .pushSpells()
            .pushRaces()
            .use(Player.class)
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;

        login();
    }

    @Test
    void success() throws Exception {
        handler.handle(session, new AddCharacterRequest(
            "Bob",
            Race.ECAFLIP,
            Gender.MALE,
            new Colors(123, 456, 789)
        ));

        Player player = dataSet.repository(Player.class).get(new Player(1));

        assertEquals(1, player.accountId());
        assertEquals(2, player.serverId());
        assertEquals("Bob", player.name());
        assertEquals(Race.ECAFLIP, player.race());
        assertEquals(Gender.MALE, player.gender());
        assertArrayEquals(new int[]{123, 456, 789}, player.colors().toArray());
        assertEquals(new Position(10300, 320), player.position());

        requestStack.assertAll(
            new CharacterCreated(),
            new CharactersList(ServerList.ONE_YEAR, Collections.singleton(
                new AccountCharacter(
                    session.account(),
                    player
                )
            ))
        );
    }

    @Test
    void error() throws Exception {
        try {
            handler.handle(session, new AddCharacterRequest(
                "-invalid-name",
                Race.ECAFLIP,
                Gender.MALE,
                new Colors(123, 456, 789)
            ));

            fail("Error packet mst be thrown");
        } catch (ErrorPacket e) {
            assertTrue(e.packet() instanceof CharacterCreationError);
            assertEquals(
                new CharacterCreationError(PlayerConstraints.Error.CREATE_CHARACTER_BAD_NAME).toString(),
                e.packet().toString()
            );
        }
    }
}
