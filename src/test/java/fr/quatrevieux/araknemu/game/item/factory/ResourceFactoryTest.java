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

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceFactoryTest extends GameBaseCase {
    private ResourceFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ResourceFactory(container.get(EffectToSpecialMapping.class));
    }

    @Test
    void createSimple() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        ItemType type = new ItemType(48, "Poudre", SuperType.RESOURCE, null);
        Item item = factory.create(template, type, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
        assertSame(type, item.type());
    }

    @Test
    void createWillFilterNonSpecialEffects() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_FIRE, 1, 2, 0, "")), 1, "", 0, "", 10);
        ItemType type = new ItemType(48, "Poudre", SuperType.RESOURCE, null);
        Item item = factory.create(template, type, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
        assertSame(type, item.type());
    }

    @Test
    void createWithSpecialEffect() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "test")), 1, "", 0, "", 10);
        ItemType type = new ItemType(48, "Poudre", SuperType.RESOURCE, null);
        Item item = factory.create(template, type, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertCount(1, item.effects());

        assertEquals(Effect.NULL1, item.effects().get(0).effect());
        assertEquals("test", item.specials().get(0).text());
    }

    @Test
    void retrieveWithSpecialEffect() {
        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        ItemType type = new ItemType(48, "Poudre", SuperType.RESOURCE, null);
        Item item = factory.retrieve(template, type, null, Arrays.asList(new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "test")));

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertCount(1, item.effects());
        assertSame(type, item.type());

        assertEquals(Effect.NULL1, item.effects().get(0).effect());
        assertEquals("test", item.specials().get(0).text());
    }
}
