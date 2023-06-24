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
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlMapTriggerRepositoryTest extends GameBaseCase {
    private SqlMapTriggerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlMapTriggerRepository(
            new ConnectionPoolExecutor(app.database().get("game"))
        );

        dataSet.use(MapTrigger.class);
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MapTrigger(741, 456, 0, null, null)));
    }

    @Test
    void getFound() throws SQLException, ContainerException {
        MapTrigger trigger = dataSet.pushTrigger(new MapTrigger(456, 123, Teleport.ACTION_ID, "741,258", ""));

        MapTrigger db = repository.get(trigger);

        assertEquals(456, db.map());
        assertEquals(123, db.cell());
        assertEquals(Teleport.ACTION_ID, db.action());
        assertEquals("741,258", db.arguments());
    }

    @Test
    void has() throws SQLException, ContainerException {
        MapTrigger trigger = dataSet.pushTrigger(new MapTrigger(456, 123, Teleport.ACTION_ID, "741,258", ""));

        assertTrue(repository.has(trigger));
        assertFalse(repository.has(new MapTrigger(1, 2, 0, null, null)));
    }

    @Test
    void findByMap() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(456, 123, Teleport.ACTION_ID, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(457, 123, Teleport.ACTION_ID, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(456, 124, Teleport.ACTION_ID, "741,258", ""));

        Collection<MapTrigger> triggers = repository.findByMap(456);

        assertCount(2, triggers);
    }

    @Test
    void all() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(456, 123, Teleport.ACTION_ID, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(457, 123, Teleport.ACTION_ID, "741,258", ""));
        dataSet.pushTrigger(new MapTrigger(456, 124, Teleport.ACTION_ID, "741,258", ""));

        Collection<MapTrigger> triggers = repository.all();

        assertCount(3, triggers);
    }
}
