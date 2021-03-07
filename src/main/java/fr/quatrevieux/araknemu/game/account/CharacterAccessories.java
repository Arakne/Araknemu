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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AbstractAccessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.NullAccessory;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * List of character accessories
 */
public final class CharacterAccessories extends AbstractAccessories {
    private final Map<AccessoryType, Accessory> accessories = new EnumMap<>(AccessoryType.class);

    public CharacterAccessories(Collection<PlayerItem> items) {
        items
            .stream()
            .map(CharacterAccessory::new)
            .forEach(this::set)
        ;
    }

    @Override
    public Accessory get(AccessoryType type) {
        if (!accessories.containsKey(type)) {
            return new NullAccessory(type);
        }

        return accessories.get(type);
    }

    private void set(Accessory accessory) {
        accessories.put(accessory.type(), accessory);
    }
}
