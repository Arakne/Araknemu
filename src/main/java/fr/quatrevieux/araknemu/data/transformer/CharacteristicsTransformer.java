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
import org.apache.commons.lang3.StringUtils;

/**
 * Transform characteristics string
 */
public final class CharacteristicsTransformer implements Transformer<Characteristics> {
    private static final int SERIALIZED_BASE    = 32;
    private static final String VALUE_SEPARATOR = ":";
    private static final String STATS_SEPARATOR = ";";

    @Override
    public String serialize(Characteristics characteristics) {
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
    public MutableCharacteristics unserialize(String serialized) {
        if (serialized == null) {
            return null;
        }

        final MutableCharacteristics characteristics = new DefaultCharacteristics();

        for (String stats : StringUtils.split(serialized, STATS_SEPARATOR)) {
            if (stats.isEmpty()) {
                continue;
            }

            final String[] data = StringUtils.split(stats, VALUE_SEPARATOR, 2);

            characteristics.set(
                Characteristic.fromId(Integer.parseInt(data[0], SERIALIZED_BASE)),
                Integer.parseInt(data[1], SERIALIZED_BASE)
            );
        }

        return characteristics;
    }
}
