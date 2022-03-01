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

package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class WearableFactoryTest extends GameBaseCase {
    private WearableFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemSets();

        factory = new WearableFactory(SuperType.AMULET, container.get(EffectToCharacteristicMapping.class), container.get(EffectToSpecialMapping.class));
    }

    @Test
    void createSimple() {
        ItemType type = new ItemType(1, "Amulette", SuperType.AMULET, null);
        Item item = factory.create(new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), type, null, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(1, item.effects());
        assertSame(type, item.type());

        Wearable wearable = (Wearable) item;

        assertCount(1, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(2, wearable.characteristics().get(0).value());
    }

    @Test
    void createRandomStats() throws ContainerException {
        ItemTemplate template = new ItemTemplate(2425, 1, "Amulette du Bouftou", 3, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550);
        ItemType type = new ItemType(1, "Amulette", SuperType.AMULET, null);
        GameItemSet set = container.get(ItemService.class).itemSet(1);

        Item item = factory.create(template, type, set, false);

        assertInstanceOf(Wearable.class, item);
        assertCount(2, item.effects());
        assertSame(template, item.template());
        assertSame(set, item.set().get());
        assertSame(type, item.type());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertBetween(1, 10, wearable.characteristics().get(0).value());
        assertEquals(Effect.ADD_STRENGTH, wearable.characteristics().get(1).effect());
        assertBetween(1, 10, wearable.characteristics().get(1).value());
    }

    @Test
    void createMaxStats() throws ContainerException {
        ItemTemplate template = new ItemTemplate(2425, 1, "Amulette du Bouftou", 3,
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"),
                new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0"),
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 0, 200, 0, "")
            ), 10, "", 1, "", 550
        );
        ItemType type = new ItemType(1, "Amulette", SuperType.AMULET, null);

        GameItemSet set = container.get(ItemService.class).itemSet(1);

        Item item = factory.create(template, type, set, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(3, item.effects());
        assertSame(template, item.template());
        assertSame(set, item.set().get());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(10, wearable.characteristics().get(0).value());
        assertEquals(Effect.ADD_STRENGTH, wearable.characteristics().get(1).effect());
        assertEquals(10, wearable.characteristics().get(1).value());

        assertCount(1, item.specials());
        assertEquals(200, item.specials().get(0).arguments()[0]);
    }

    @Test
    void createSpecial() {
        ItemTemplate template = new ItemTemplate(2425, 1, "Amulette du Bouftou", 3, Arrays.asList(
            new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"),
            new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0"),
            new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "")
        ), 10, "", 1, "", 550);
        ItemType type = new ItemType(1, "Amulette", SuperType.AMULET, null);

        Item item = factory.create(template, type, null, true);

        assertInstanceOf(Wearable.class, item);
        assertCount(3, item.effects());
        assertSame(template, item.template());

        Wearable wearable = (Wearable) item;

        assertCount(2, wearable.characteristics());
        assertCount(1, wearable.specials());
        assertEquals(Effect.NULL1, wearable.specials().get(0).effect());
    }

    @Test
    void retrieve() throws ContainerException {
        GameItemSet set = container.get(ItemService.class).itemSet(1);
        ItemType type = new ItemType(1, "Amulette", SuperType.AMULET, null);

        Item item = factory.retrieve(
            new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100),
            type,
            set,
            Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 20, 0, 0, ""))
        );

        assertInstanceOf(Wearable.class, item);
        assertCount(1, item.effects());
        assertSame(type, item.type());

        Wearable wearable = (Wearable) item;

        assertCount(1, wearable.characteristics());
        assertEquals(Effect.ADD_INTELLIGENCE, wearable.characteristics().get(0).effect());
        assertEquals(20, wearable.characteristics().get(0).value());
        assertSame(set, item.set().get());
    }
}
