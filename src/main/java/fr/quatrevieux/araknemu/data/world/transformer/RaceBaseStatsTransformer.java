package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
final public class RaceBaseStatsTransformer implements Transformer<SortedMap<Integer, Characteristics>> {
    final private Transformer<Characteristics> characteristicsTransformer;

    public RaceBaseStatsTransformer(Transformer<Characteristics> characteristicsTransformer) {
        this.characteristicsTransformer = characteristicsTransformer;
    }

    @Override
    public String serialize(SortedMap<Integer, Characteristics> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<Integer, Characteristics> unserialize(String serialize) {
        SortedMap<Integer, Characteristics> stats = new TreeMap<>(Collections.reverseOrder());

        for (String levelStats : StringUtils.split(serialize, "|")) {
            String[] parts = StringUtils.split(levelStats, "@", 2);

            stats.put(
                Integer.parseUnsignedInt(parts[1]),
                characteristicsTransformer.unserialize(parts[0])
            );
        }

        return stats;
    }
}
