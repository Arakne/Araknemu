package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Transformer for {@link BoostStatsData}
 */
final public class BoostStatsDataTransformer implements Transformer<BoostStatsData> {
    @Override
    public String serialize(BoostStatsData value) {
        return null;
    }

    @Override
    public BoostStatsData unserialize(String serialize) {
        Map<Characteristic, List<BoostStatsData.Interval>> data = new EnumMap<>(Characteristic.class);

        for (String characteristicData : StringUtils.split(serialize, ";")) {
            String[] entry = StringUtils.split(characteristicData, ":", 2);

            Characteristic characteristic = Characteristic.fromId(Integer.parseInt(entry[0]));
            List<BoostStatsData.Interval> intervals = new ArrayList<>();

            for (String intervalData : StringUtils.split(entry[1], ",")) {
                String[] parts = StringUtils.split(intervalData, "@", 3);

                intervals.add(
                    new BoostStatsData.Interval(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        parts.length == 3 ? Integer.parseInt(parts[2]) : 1
                    )
                );
            }

            intervals.sort(Comparator.comparingInt(BoostStatsData.Interval::start));


            data.put(characteristic, intervals);
        }

        return new BoostStatsData(data);
    }
}
