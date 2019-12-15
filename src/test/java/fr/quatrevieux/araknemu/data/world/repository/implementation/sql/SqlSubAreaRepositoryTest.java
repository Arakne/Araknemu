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

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.SqlSubAreaRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class SqlSubAreaRepositoryTest extends GameBaseCase {
    private SqlSubAreaRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(SubArea.class);
        repository = new SqlSubAreaRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new SubArea(45, 0, "", false, null)));
    }

    @Test
    void getFound() throws SQLException, ContainerException {
        dataSet.pushSubArea(new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.NONE));

        SubArea area = repository.get(new SubArea(4, 0, null, false, null));

        assertEquals(4, area.id());
        assertEquals(0, area.area());
        assertEquals("La forêt d'Amakna", area.name());
        assertEquals(true, area.conquestable());
        assertEquals(Alignment.NONE, area.alignment());
    }

    @Test
    void has() throws SQLException, ContainerException {
        SubArea area = new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.NONE);

        assertFalse(repository.has(area));

        dataSet.pushSubArea(area);

        assertTrue(repository.has(area));
    }

    @Test
    void all() throws SQLException, ContainerException {
        dataSet.pushSubArea(new SubArea(1, 0, "Port de Madrestam", true, Alignment.NONE));
        dataSet.pushSubArea(new SubArea(2, 0, "La montagne des Craqueleurs", true, Alignment.NEUTRAL));
        dataSet.pushSubArea(new SubArea(3, 0, "Le champ des Ingalsses", true, Alignment.BONTARIAN));
        dataSet.pushSubArea(new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.BRAKMARIAN));

        Collection<SubArea> areas = repository.all();

        assertCount(4, areas);
        assertContainsOnly(SubArea.class, areas.toArray());
    }
}
