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

package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * Class for simple equipments
 */
public final class Wearable extends AbstractEquipment {
    public Wearable(ItemTemplate template, ItemType type, @Nullable GameItemSet set, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        super(template, type, set, characteristics, specials);
    }
}
