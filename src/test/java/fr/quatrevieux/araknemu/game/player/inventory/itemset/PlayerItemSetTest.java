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

package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerItemSetTest extends GameBaseCase {
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemSets()
            .pushItemTemplates()
        ;

        service = container.get(ItemService.class);
    }

    @Test
    void empty() {
        PlayerItemSet set = new PlayerItemSet(
            service.itemSet(1)
        );

        assertEquals(1, set.id());
        assertTrue(set.isEmpty());
        assertCount(0, set.bonus().characteristics());
        assertCount(0, set.bonus().specials());
        assertCount(0, set.bonus().effects());
        assertCount(0, set.items());
    }

    @Test
    void add() throws ContainerException {
        PlayerItemSet set = new PlayerItemSet(service.itemSet(1));

        ItemTemplate template = container.get(ItemTemplateRepository.class).get(2425);
        set.add(template);

        assertEquals(1, set.id());
        assertFalse(set.isEmpty());
        assertCount(0, set.bonus().characteristics());
        assertCount(0, set.bonus().specials());
        assertCount(0, set.bonus().effects());
        assertCount(1, set.items());
        assertContainsAll(set.items(), template);

        ItemTemplate template1 = container.get(ItemTemplateRepository.class).get(2411);
        set.add(template1);
        assertCount(2, set.items());
        assertContainsAll(set.items(), template, template1);

        assertCount(2, set.bonus().characteristics());
        assertCount(2, set.bonus().effects());

        assertEquals(Effect.ADD_STRENGTH, set.bonus().characteristics().get(0).effect());
        assertEquals(5, set.bonus().characteristics().get(0).value());
        assertEquals(Effect.ADD_INTELLIGENCE, set.bonus().characteristics().get(1).effect());
        assertEquals(5, set.bonus().characteristics().get(1).value());
    }

    @Test
    void apply() throws ContainerException {
        PlayerItemSet set = new PlayerItemSet(service.itemSet(1));

        set.add(container.get(ItemTemplateRepository.class).get(2411));
        set.add(container.get(ItemTemplateRepository.class).get(2414));

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        set.apply(characteristics);

        assertEquals(5, characteristics.get(Characteristic.STRENGTH));
        assertEquals(5, characteristics.get(Characteristic.INTELLIGENCE));
    }

    @Test
    void applySpecials() throws ContainerException, SQLException {
        PlayerItemSet set = new PlayerItemSet(service.itemSet(60));

        set.add(container.get(ItemTemplateRepository.class).get(8213));
        set.add(container.get(ItemTemplateRepository.class).get(8219));
        set.add(container.get(ItemTemplateRepository.class).get(8225));
        set.add(container.get(ItemTemplateRepository.class).get(8231));
        set.add(container.get(ItemTemplateRepository.class).get(8237));
        set.add(container.get(ItemTemplateRepository.class).get(8243));

        GamePlayer player = gamePlayer();

        set.applySpecials(player);

        assertEquals(60, player.properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void applyCurrentBonus() throws ContainerException, SQLException {
        GamePlayer player = gamePlayer();
        PlayerItemSet set = new PlayerItemSet(service.itemSet(60));

        set.add(container.get(ItemTemplateRepository.class).get(8213));
        set.add(container.get(ItemTemplateRepository.class).get(8219));
        set.add(container.get(ItemTemplateRepository.class).get(8225));
        set.add(container.get(ItemTemplateRepository.class).get(8231));
        set.add(container.get(ItemTemplateRepository.class).get(8237));

        set.applySpecials(player);
        assertEquals(30, player.properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));

        set.add(container.get(ItemTemplateRepository.class).get(8243));
        set.applyCurrentBonus(player);

        assertEquals(60, player.properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void relieveLastBonus() throws ContainerException, SQLException {
        GamePlayer player = gamePlayer();
        PlayerItemSet set = new PlayerItemSet(service.itemSet(60));

        player.properties().characteristics().specials().add(SpecialEffects.Type.INITIATIVE, 60);

        set.add(container.get(ItemTemplateRepository.class).get(8213));
        set.add(container.get(ItemTemplateRepository.class).get(8219));
        set.add(container.get(ItemTemplateRepository.class).get(8225));
        set.add(container.get(ItemTemplateRepository.class).get(8231));
        set.add(container.get(ItemTemplateRepository.class).get(8237));

        set.relieveLastBonus(player);
        assertEquals(30, player.properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }
}
