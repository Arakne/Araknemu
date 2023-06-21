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

package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateItemSetTest extends GameBaseCase {
    private ItemService service;
    private ItemTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemSets()
            .pushItemTemplates()
        ;
        service = container.get(ItemService.class);
        repository = container.get(ItemTemplateRepository.class);
    }

    @Test
    void generateWithEmptySet() {
        assertEquals(
            "OS-1",
            new UpdateItemSet(
                new PlayerItemSet(
                    service.itemSet(1)
                )
            ).toString()
        );
    }

    @Test
    void generateWithItems() {
        assertEquals(
            "OS+1|2425;2411|76#5#0#0#,7e#5#0#0#",
            new UpdateItemSet(
                new PlayerItemSet(
                    service.itemSet(1),
                    new HashSet<>(Arrays.asList(
                        repository.get(2425),
                        repository.get(2411)
                    ))
                )
            ).toString()
        );
    }
}
