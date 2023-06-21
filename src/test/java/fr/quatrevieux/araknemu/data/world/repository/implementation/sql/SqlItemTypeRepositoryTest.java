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
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.transformer.EffectAreaTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.SuperType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlItemTypeRepositoryTest extends GameBaseCase {
    private SqlItemTypeRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new SqlItemTypeRepository(new ConnectionPoolExecutor(app.database().get("game")), new EffectAreaTransformer());

        dataSet.pushItemTypes();
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        ItemType type = repository.get(1);

        assertEquals(1, type.id());
        assertEquals("Amulette", type.name());
        assertSame(SuperType.AMULET, type.superType());
        assertNull(type.effectArea());
    }

    @Test
    void getWithEffectArea() {
        ItemType type = repository.get(7);

        assertEquals(7, type.id());
        assertEquals("Marteau", type.name());
        assertSame(SuperType.WEAPON, type.superType());
        assertEquals(new EffectArea(EffectArea.Type.CROSS, 1), type.effectArea());
    }

    @Test
    void getByEntity() {
        ItemType type = repository.get(new ItemType(1, null, null, null));

        assertEquals(1, type.id());
        assertEquals("Amulette", type.name());
        assertSame(SuperType.AMULET, type.superType());
        assertNull(type.effectArea());
    }

    @Test
    void has() {
        assertTrue(repository.has(new ItemType(1, null, null, null)));
        assertFalse(repository.has(new ItemType(-5, null, null, null)));
    }

    @Test
    void load() {
        Collection<ItemType> types = repository.load();

        assertCount(114, types);
    }
}