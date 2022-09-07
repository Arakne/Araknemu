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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Apply computed characteristics over a {@link MutableCharacteristics}
 */
public final class MutableComputedCharacteristics extends ComputedCharacteristics<MutableCharacteristics> implements MutableCharacteristics {
    public MutableComputedCharacteristics(MutableCharacteristics inner) {
        super(inner);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        inner.set(characteristic, value);
    }

    @Override
    public void add(Characteristic characteristic, int value) {
        inner.add(characteristic, value);
    }
}
