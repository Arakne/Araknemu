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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlPlayerExperienceRepositoryTest extends GameBaseCase {
    private SqlPlayerExperienceRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushExperience();

        repository = new SqlPlayerExperienceRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new PlayerExperience(741, 0)));
    }

    @Test
    void getFound() {
        PlayerExperience xp = repository.get(new PlayerExperience(12, 0));

        assertEquals(12, xp.level());
        assertEquals(32600, xp.experience());
    }

    @Test
    void has() {
        assertTrue(repository.has(new PlayerExperience(150, 0)));
        assertFalse(repository.has(new PlayerExperience(256, 0)));
    }

    @Test
    void all() {
        List<PlayerExperience> levels = repository.all();

        assertCount(200, levels);

        assertEquals(1, levels.get(0).level());
        assertEquals(200, levels.get(199).level());
    }
}
