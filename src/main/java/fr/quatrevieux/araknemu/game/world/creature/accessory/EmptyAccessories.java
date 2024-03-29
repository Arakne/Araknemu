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

package fr.quatrevieux.araknemu.game.world.creature.accessory;

import java.util.Collections;
import java.util.List;

/**
 * Empty accessories
 */
public final class EmptyAccessories implements Accessories {
    @Override
    public Accessory get(AccessoryType type) {
        return NullAccessory.from(type);
    }

    @Override
    public List<Accessory> all() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "";
    }
}
