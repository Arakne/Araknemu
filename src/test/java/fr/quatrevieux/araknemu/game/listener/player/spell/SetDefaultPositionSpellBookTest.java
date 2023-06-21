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

package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreated;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetDefaultPositionSpellBookTest extends GameBaseCase {
    private SetDefaultPositionSpellBook listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushSpells()
            .pushRaces()
            .use(PlayerSpell.class)
        ;

        listener = new SetDefaultPositionSpellBook(
            container.get(PlayerRaceService.class),
            container.get(PlayerSpellRepository.class)
        );
    }

    @Test
    void onCharacterCreated() throws ContainerException {
        Player player;

        listener.on(
            new CharacterCreated(
                new AccountCharacter(
                    new GameAccount(
                        new Account(1),
                        container.get(AccountService.class),
                        2
                    ),
                    player = dataSet.pushPlayer("Robert", 1, 2)
                )
            )
        );

        List<PlayerSpell> spells = new ArrayList<>(container.get(PlayerSpellRepository.class).byPlayer(player));

        assertCount(3, spells);

        assertEquals(3, spells.get(0).spellId());
        assertEquals(1, spells.get(0).position());
        assertEquals(6, spells.get(1).spellId());
        assertEquals(2, spells.get(1).position());
        assertEquals(17, spells.get(2).spellId());
        assertEquals(3, spells.get(2).position());
    }
}