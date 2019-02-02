package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.util.Base64;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Transform map fight places
 */
final public class FightPlacesTransformer implements Transformer<List<Integer>[]> {
    @Override
    public String serialize(List<Integer>[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer>[] unserialize(String serialize) {
        try {
            return Arrays.stream(StringUtils.split(serialize, "|", 2))
                .map(this::parseTeamPlaces)
                .toArray(List[]::new)
            ;
        } catch (RuntimeException e) {
            throw new TransformerException("Cannot parse places '" + serialize + "'", e);
        }
    }

    private List<Integer> parseTeamPlaces(String places) {
        List<Integer> cells = new ArrayList<>(places.length() / 2);

        for (int i = 0; i < places.length(); i += 2) {
            cells.add(Base64.decode(places.substring(i, i + 2)));
        }

        return cells;
    }
}
