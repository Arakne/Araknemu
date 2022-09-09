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

package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemServiceTest extends GameBaseCase {
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        service = new ItemService(
            container.get(ItemTemplateRepository.class),
            container.get(ItemFactory.class),
            container.get(ItemSetRepository.class),
            container.get(ItemTypeRepository.class),
            container.get(EffectToCharacteristicMapping.class),
            container.get(EffectToSpecialMapping.class)
        );
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading item sets...");
        Mockito.verify(logger).info("Successfully load {} item sets", 3);

        Mockito.verify(logger).info("Loading items...");
        Mockito.verify(logger).info("Successfully load {} items", 18);
    }

    @Test
    void create() {
        Item item = service.create(39);

        assertEquals(39, item.template().id());
        assertInstanceOf(Wearable.class, item);
        assertEquals(1, item.template().type());
        assertEquals(1, item.type().id());
        assertEquals(SuperType.AMULET, item.type().superType());
        assertCount(1, item.effects());

        Wearable wearable = (Wearable) item;

        assertEquals(Characteristic.INTELLIGENCE, wearable.characteristics().get(0).characteristic());
        assertEquals(2, wearable.characteristics().get(0).boost());
    }

    @Test
    void createRandomStats() {
        Item item1 = service.create(2425);
        Item item2 = service.create(2425);

        assertNotEquals(item1, item2);
    }

    @Test
    void createMaxStats() {
        Item item1 = service.create(2425, true);
        Item item2 = service.create(2425, true);

        assertEquals(item1, item2);

        Wearable wearable = (Wearable) item1;

        assertEquals(Characteristic.INTELLIGENCE, wearable.characteristics().get(0).characteristic());
        assertEquals(10, wearable.characteristics().get(0).boost());
        assertEquals(Characteristic.STRENGTH, wearable.characteristics().get(1).characteristic());
        assertEquals(10, wearable.characteristics().get(1).boost());
    }

    @Test
    void createWithItemTemplate() {
        ItemTemplate template = container.get(ItemTemplateRepository.class).get(39);

        Item item = service.create(template);

        assertSame(template, item.template());
        assertInstanceOf(Wearable.class, item);
        assertEquals(1, item.type().id());
        assertEquals(SuperType.AMULET, item.type().superType());
        assertCount(1, item.effects());

        Wearable wearable = (Wearable) item;

        assertEquals(Characteristic.INTELLIGENCE, wearable.characteristics().get(0).characteristic());
        assertEquals(2, wearable.characteristics().get(0).boost());
    }

    @Test
    void createBulkWithFixedStats() {
        ItemTemplate template = container.get(ItemTemplateRepository.class).get(39);

        Map<Item, Integer> items = service.createBulk(template, 3);

        assertEquals(1, items.size());
        assertTrue(items.containsKey(service.create(39)));
        assertEquals(3, (int) items.get(service.create(39)));
    }

    @Test
    void createBulkWithRandomStats() {
        ItemTemplate template = container.get(ItemTemplateRepository.class).get(2422);
        Map<Item, Integer> items = service.createBulk(template, 3);

        assertEquals(3, items.size());
        assertTrue(items.entrySet().stream().allMatch(entry-> entry.getKey().template().equals(template) && entry.getValue() == 1));
    }

    @Test
    void retrieve() {
        Item item = service.create(2425);

        assertEquals(
            item,
            service.retrieve(
                2425,
                item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList())
            )
        );

        assertSame(
            item.set().get(),
            service.itemSet(1)
        );
    }

    @Test
    void createWithItemSet() {
        Item item = service.create(2425);

        assertTrue(item.set().isPresent());
        assertEquals(1, item.set().get().id());
    }

    @Test
    void createWithoutItemSet() {
        Item item = service.create(39);

        assertFalse(item.set().isPresent());
    }

    @Test
    void itemSet() {
        GameItemSet set = service.itemSet(1);

        assertEquals(1, set.id());

        assertCount(2, set.bonus(2).effects());
        assertCount(2, set.bonus(2).characteristics());
        assertEquals(Effect.ADD_STRENGTH, set.bonus(2).characteristics().get(0).effect());
        assertEquals(5, set.bonus(2).characteristics().get(0).value());
        assertEquals(Effect.ADD_INTELLIGENCE, set.bonus(2).characteristics().get(1).effect());
        assertEquals(5, set.bonus(2).characteristics().get(1).value());

        assertCount(6, set.bonus(7).effects());
        assertCount(6, set.bonus(7).characteristics());
        assertEquals(Effect.ADD_VITALITY, set.bonus(7).characteristics().get(0).effect());
        assertEquals(30, set.bonus(7).characteristics().get(0).value());
        assertEquals(Effect.ADD_WISDOM, set.bonus(7).characteristics().get(1).effect());
        assertEquals(20, set.bonus(7).characteristics().get(1).value());
        assertEquals(Effect.ADD_STRENGTH, set.bonus(7).characteristics().get(2).effect());
        assertEquals(50, set.bonus(7).characteristics().get(2).value());
        assertEquals(Effect.ADD_INTELLIGENCE, set.bonus(7).characteristics().get(3).effect());
        assertEquals(50, set.bonus(7).characteristics().get(3).value());
        assertEquals(Effect.ADD_ACTION_POINTS, set.bonus(7).characteristics().get(4).effect());
        assertEquals(1, set.bonus(7).characteristics().get(4).value());
        assertEquals(Effect.ADD_DAMAGE, set.bonus(7).characteristics().get(5).effect());
        assertEquals(5, set.bonus(7).characteristics().get(5).value());
    }

    @Test
    void name() {
        assertEquals("item", service.name());
    }
}
