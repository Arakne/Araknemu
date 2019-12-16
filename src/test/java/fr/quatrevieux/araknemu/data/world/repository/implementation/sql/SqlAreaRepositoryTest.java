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
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlAreaRepositoryTest extends GameBaseCase {
    private SqlAreaRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Area.class);
        repository = new SqlAreaRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new Area(45, null, 0)));
    }

    @Test
    void getFound() throws SQLException, ContainerException {
        dataSet.pushArea(new Area(0, "Amakna", 0));

        Area area = repository.get(new Area(0, null, 0));

        assertEquals(0, area.id());
        assertEquals(0, area.superarea());
        assertEquals("Amakna", area.name());
    }

    @Test
    void has() throws SQLException, ContainerException {
        Area area = new Area(0, "Amakna", 0);

        assertFalse(repository.has(area));

        dataSet.pushArea(area);

        assertTrue(repository.has(area));
    }

    @Test
    void all() throws SQLException, ContainerException {
        dataSet.pushAreas();

        Collection<Area> areas = repository.all();

        assertCount(3, areas);
        assertContainsOnly(Area.class, areas.toArray());
    }
}
