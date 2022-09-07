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
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToUseMapping;
import fr.quatrevieux.araknemu.game.item.effect.use.AddLifeEffect;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class UsableFactoryTest extends GameBaseCase {
    private UsableFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new UsableFactory(
            container.get(EffectToUseMapping.class),
            container.get(EffectToSpecialMapping.class)
        );
    }

    @Test
    void create() {
        ItemType type = new ItemType(12, "Potion", SuperType.USABLE, null);
        Item item = factory.create(
            new ItemTemplate(283, 12, "Fiole de Soin", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_LIFE, 31, 60, 0, "1d30+30")), 1, "", 0, "", 10),
            type,
            null,
            false
        );

        assertInstanceOf(UsableItem.class, item);
        assertCount(1, item.effects());
        assertEquals(new UseEffect(new AddLifeEffect(), Effect.ADD_LIFE, new int[] {31, 60, 0}), item.effects().get(0));
        assertSame(type, item.type());
    }

    @Test
    void retrieve() {
        ItemType type = new ItemType(12, "Potion", SuperType.USABLE, null);
        Item item = factory.retrieve(
            new ItemTemplate(283, 12, "Fiole de Soin", 10, new ArrayList<>(), 1, "", 0, "", 10),
            type,
            null,
            Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_LIFE, 31, 60, 0, "1d30+30"))
        );

        assertInstanceOf(UsableItem.class, item);
        assertCount(1, item.effects());
        assertSame(type, item.type());
        assertEquals(new UseEffect(new AddLifeEffect(), Effect.ADD_LIFE, new int[] {31, 60, 0}), item.effects().get(0));
    }
}
