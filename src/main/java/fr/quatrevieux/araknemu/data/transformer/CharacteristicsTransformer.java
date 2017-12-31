package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.apache.commons.lang3.StringUtils;

/**
 * Transform characteristics string
 */
final public class CharacteristicsTransformer {
    final static private int SERIALIZED_BASE    = 32;
    final static private String VALUE_SEPARATOR = ":";
    final static private String STATS_SEPARATOR = ";";

    public String serialize(Characteristics characteristics) {
        if (characteristics == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (Characteristic characteristic : Characteristic.values()) {
            int value = characteristics.get(characteristic);

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

    public MutableCharacteristics unserialize(String serialized) {
        if (serialized == null) {
            return null;
        }

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        for (String stats : StringUtils.split(serialized, STATS_SEPARATOR)) {
            if (stats.isEmpty()) {
                continue;
            }

            String[] data = StringUtils.split(stats, VALUE_SEPARATOR, 2);

            characteristics.set(
                Characteristic.fromId(Integer.parseInt(data[0], SERIALIZED_BASE)),
                Integer.parseInt(data[1], SERIALIZED_BASE)
            );
        }

        return characteristics;
    }
}
