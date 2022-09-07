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

package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import fr.quatrevieux.araknemu.util.Splitter;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Transform characteristics string
 */
public final class CharacteristicsTransformer implements Transformer<Characteristics> {
    private static final int SERIALIZED_BASE    = 32;
    private static final char VALUE_SEPARATOR = ':';
    private static final char STATS_SEPARATOR = ';';

    @Override
    public @PolyNull String serialize(@PolyNull Characteristics characteristics) {
        if (characteristics == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();

        for (Characteristic characteristic : Characteristic.values()) {
            final int value = characteristics.get(characteristic);

            if (value == 0) {
                continue;
            }

            sb
                .append(Integer.toString(characteristic.id(), SERIALIZED_BASE))
                .append(VALUE_SEPARATOR)
                .append(Integer.toString(value, SERIALIZED_BASE))
                .append(STATS_SEPARATOR)
            ;
        }

        return sb.toString();
    }

    @Override
    public @PolyNull MutableCharacteristics unserialize(@PolyNull String serialized) {
        if (serialized == null) {
            return null;
        }

        final MutableCharacteristics characteristics = new DefaultCharacteristics();
        final Splitter splitter = new Splitter(serialized, STATS_SEPARATOR);

        while (splitter.hasNext()) {
            final String sData = splitter.nextPart();

            if (sData.isEmpty()) {
                continue;
            }

            final Splitter data = new Splitter(sData, VALUE_SEPARATOR);

            characteristics.set(
                Characteristic.fromId(data.nextInt(SERIALIZED_BASE)),
                data.nextInt(SERIALIZED_BASE)
            );
        }

        return characteristics;
    }
}
