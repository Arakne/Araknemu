package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.data.value.Colors;
import org.apache.commons.lang3.StringUtils;

/**
 * Transform {@link Colors} database data
 *
 * Colors are 3 hexa numbers, separated by a comma ","
 * Default color is "-1"
 *
 * Ex: ffebcd,-1,ffebcd
 */
final public class ColorsTransformer implements Transformer<Colors> {
    @Override
    public String serialize(Colors value) {
        return StringUtils.join(value.toHexArray(), ",");
    }

    @Override
    public Colors unserialize(String serialize) throws TransformerException {
        if (serialize.equals("-1,-1,-1")) {
            return Colors.DEFAULT;
        }

        String[] parts = StringUtils.split(serialize, ",", 3);

        if (parts.length != 3) {
            throw new TransformerException("Invalid color expression '" + serialize + "'");
        }

        return new Colors(
            parts[0].equals("-1") ? -1 : Integer.parseInt(parts[0], 16),
            parts[1].equals("-1") ? -1 : Integer.parseInt(parts[1], 16),
            parts[2].equals("-1") ? -1 : Integer.parseInt(parts[2], 16)
        );
    }
}
