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

package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellMove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveSpellTest extends GameBaseCase {
    private MoveSpell handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new MoveSpell();

        gamePlayer(true);
    }

    @Test
    void moveSpellToFreePlace() throws Exception {
        handler.handle(
            session,
            new SpellMove(3, 5)
        );

        assertEquals(5, gamePlayer().properties().spells().entry(3).position());

        assertEquals(5, dataSet.refresh(gamePlayer().properties().spells().entry(3).entity()).position());
    }

    @Test
    void moveSpellWithAlreadyTakenPlace() throws Exception {
        gamePlayer().properties().spells().entry(17).move(2);
        handler.handle(
            session,
            new SpellMove(3, 2)
        );

        assertEquals(2, gamePlayer().properties().spells().entry(3).position());
        assertEquals(63, gamePlayer().properties().spells().entry(17).position());

        assertEquals(2, dataSet.refresh(gamePlayer().properties().spells().entry(3).entity()).position());
        assertEquals(63, dataSet.refresh(gamePlayer().properties().spells().entry(17).entity()).position());
    }
}
