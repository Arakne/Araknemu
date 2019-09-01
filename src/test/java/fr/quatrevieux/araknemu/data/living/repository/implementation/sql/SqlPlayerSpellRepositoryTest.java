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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlPlayerSpellRepositoryTest extends GameBaseCase {
    private SqlPlayerSpellRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerSpell.class);

        repository = new SqlPlayerSpellRepository(
            new ConnectionPoolExecutor(app.database().get("game"))
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new PlayerSpell(123, 0, false, 0, 5)));
    }

    @Test
    void addAndGet() {
        PlayerSpell spell = repository.add(
            new PlayerSpell(1, 3, true, 5, 3)
        );

        spell = repository.get(spell);

        assertEquals(1, spell.playerId());
        assertEquals(3, spell.spellId());
        assertTrue(spell.classSpell());
        assertEquals(3, spell.position());
        assertEquals(5, spell.level());
    }

    @Test
    void has() {
        PlayerSpell spell = repository.add(
            new PlayerSpell(1, 3, true, 5, 3)
        );

        assertTrue(repository.has(spell));
        assertFalse(repository.has(new PlayerSpell(0, 0, false, 0, 0)));
    }

    @Test
    void addForReplace() {
        PlayerSpell spell = repository.add(
            new PlayerSpell(1, 3, true, 1, 3)
        );

        spell.setLevel(5);
        spell.setPosition(4);

        repository.add(spell);

        spell = repository.get(spell);

        assertEquals(1, spell.playerId());
        assertEquals(3, spell.spellId());
        assertTrue(spell.classSpell());
        assertEquals(5, spell.level());
        assertEquals(4, spell.position());
    }

    @Test
    void delete() {
        PlayerSpell spell = repository.add(
            new PlayerSpell(1, 3, true, 1, 3)
        );

        repository.delete(spell);

        assertFalse(repository.has(spell));
    }

    @Test
    void deleteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(
            new PlayerSpell(1, 3, true, 1, 3)
        ));
    }

    @Test
    void byPlayer() {
        repository.add(new PlayerSpell(1, 3, true, 1, 3));
        repository.add(new PlayerSpell(1, 9, true, 1, 63));
        repository.add(new PlayerSpell(8, 9, true, 1, 63));


        Collection<PlayerSpell> items = repository.byPlayer(new Player(1));

        assertCount(2, items);
    }
}
