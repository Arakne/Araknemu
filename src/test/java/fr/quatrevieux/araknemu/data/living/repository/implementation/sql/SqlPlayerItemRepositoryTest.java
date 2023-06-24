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

import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlPlayerItemRepositoryTest extends GameBaseCase {
    private SqlPlayerItemRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);

        repository = new SqlPlayerItemRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            new ItemEffectsTransformer()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new PlayerItem(123, 123, 0, null, 0, 0)));
    }

    @Test
    void addAndGet() {
        PlayerItem item = repository.add(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -1
            )
        );

        item = repository.get(item);

        assertEquals(1, item.playerId());
        assertEquals(3, item.entryId());
        assertEquals(39, item.itemTemplateId());
        assertEquals(-1, item.position());
        assertEquals(5, item.quantity());
    }

    @Test
    void getShouldFixInvalidPosition() {
        PlayerItem item1 = repository.add(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -5
            )
        );
        PlayerItem item2 = repository.add(
            new PlayerItem(
                1,
                10,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                300
            )
        );

        item1 = repository.get(item1);
        item2 = repository.get(item2);

        assertEquals(-1, item1.position());
        assertEquals(-1, item2.position());
    }

    @Test
    void has() {
        PlayerItem item = repository.add(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -1
            )
        );

        assertTrue(repository.has(item));
        assertFalse(repository.has(new PlayerItem(0, 0, 0, null, 0, 0)));
    }

    @Test
    void update() {
        PlayerItem item = repository.add(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -1
            )
        );

        item.setPosition(0);
        item.setQuantity(1);

        repository.update(item);

        item = repository.get(item);

        assertEquals(1, item.playerId());
        assertEquals(3, item.entryId());
        assertEquals(39, item.itemTemplateId());
        assertEquals(0, item.position());
        assertEquals(1, item.quantity());
    }

    @Test
    void updateNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.update(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -1
            )
        ));
    }

    @Test
    void delete() {
        PlayerItem item = repository.add(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -1
            )
        );

        repository.delete(item);

        assertFalse(repository.has(item));
    }

    @Test
    void deleteNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.delete(
            new PlayerItem(
                1,
                3,
                39,
                Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
                5,
                -1
            )
        ));
    }

    @Test
    void byPlayer() {
        repository.add(new PlayerItem(
            1,
            3,
            39,
            Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
            1,
            0
        ));

        repository.add(new PlayerItem(
            1,
            5,
            39,
            Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
            5,
            -1
        ));

        repository.add(new PlayerItem(
            5,
            6,
            123,
            Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")),
            2,
            1
        ));

        Collection<PlayerItem> items = repository.byPlayer(new Player(1));

        assertCount(2, items);
    }

    @Test
    void forCharacterList() throws ContainerException {
        repository.add(new PlayerItem(1, 1, 12, new ArrayList<>(), 1, 0));
        repository.add(new PlayerItem(1, 2, 23, new ArrayList<>(), 1, 1));
        repository.add(new PlayerItem(1, 3, 45, new ArrayList<>(), 1, 6));
        repository.add(new PlayerItem(1, 4, 45, new ArrayList<>(), 1, -1));
        repository.add(new PlayerItem(2, 5, 45, new ArrayList<>(), 1, 1));
        repository.add(new PlayerItem(2, 6, 45, new ArrayList<>(), 1, 6));
        repository.add(new PlayerItem(3, 7, 45, new ArrayList<>(), 1, 0));
        repository.add(new PlayerItem(4, 8, 45, new ArrayList<>(), 1, 0));

        dataSet.pushPlayer("One", 1, 2);
        dataSet.pushPlayer("Two", 1, 2);
        dataSet.pushPlayer("BadServer", 1, 1);
        dataSet.pushPlayer("BadAccount", 2, 2);

        Map<Integer, List<PlayerItem>> result = repository.forCharacterList(2, 1, new int[] {0, 1, 6});

        assertEquals(2, result.size());

        assertCount(3, result.get(1));
        assertEquals(1, result.get(1).get(0).entryId());
        assertEquals(2, result.get(1).get(1).entryId());
        assertEquals(3, result.get(1).get(2).entryId());

        assertCount(2, result.get(2));
        assertEquals(5, result.get(2).get(0).entryId());
        assertEquals(6, result.get(2).get(1).entryId());
    }
}
