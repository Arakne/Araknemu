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
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultItemFactoryTest extends GameBaseCase {
    @Test
    void typeNotFound() {
        DefaultItemFactory factory = new DefaultItemFactory();

        ItemType type = new ItemType(1, "Amulette", SuperType.AMULET, null);

        assertThrows(NoSuchElementException.class, () -> factory.create(new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), type, null, false), "Invalid type AMULETTE");
        assertThrows(NoSuchElementException.class, () -> factory.retrieve(new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100), type, null, new ArrayList<>()), "Invalid type AMULETTE");
    }

    @Test
    void createSuccess() throws ContainerException {
        DefaultItemFactory factory = new DefaultItemFactory(
            new ResourceFactory(container.get(EffectToSpecialMapping.class))
        );

        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        ItemType type = new ItemType(48, "Poudre", SuperType.RESOURCE, null);
        Item item = factory.create(template, type, null, false);

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
        assertSame(type, item.type());
    }

    @Test
    void retrieveSuccess() throws ContainerException {
        DefaultItemFactory factory = new DefaultItemFactory(
            new ResourceFactory(container.get(EffectToSpecialMapping.class))
        );

        ItemTemplate template = new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10);
        ItemType type = new ItemType(48, "Poudre", SuperType.RESOURCE, null);
        Item item = factory.retrieve(template, type, null, new ArrayList<>());

        assertInstanceOf(Resource.class, item);
        assertSame(template, item.template());
        assertTrue(item.effects().isEmpty());
        assertSame(type, item.type());
    }
}
