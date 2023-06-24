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

package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class NpcStoreServiceTest extends GameBaseCase {
    private NpcStoreService service;
    private NpcTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushNpcs()
            .pushNpcWithStore()
        ;

        repository = container.get(NpcTemplateRepository.class);
        service = new NpcStoreService(
            container.get(ItemService.class),
            container.get(ItemTemplateRepository.class),
            configuration.economy()
        );
    }

    @Test
    void loadWithoutStore() {
        assertFalse(service.load(repository.get(848)).isPresent());
    }

    @Test
    void loadWithStore() {
        NpcStore store = service.load(repository.get(10001)).get();

        assertArrayEquals(new int[] {39, 2425}, store.available().stream().mapToInt(ItemTemplate::id).toArray());
        assertSame(store, service.load(repository.get(10001)).get());
    }
}
