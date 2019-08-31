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

package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SpellListTest extends GameBaseCase {
    @Test
    void generate() throws ContainerException, SQLException {
        dataSet.pushSpells();

        SpellBook book = new SpellBook(
            new DefaultListenerAggregate(),
            dataSet.createPlayer(1),
            Arrays.asList(
                new SpellBookEntry(
                    new PlayerSpell(1, 3, true, 5, 1),
                    container.get(SpellService.class).get(3)
                ),
                new SpellBookEntry(
                    new PlayerSpell(1, 6, true, 2, 3),
                    container.get(SpellService.class).get(6)
                )
            )
        );

        assertEquals(
            "SL3~5~b;6~2~d",
            new SpellList(book).toString()
        );
    }
}