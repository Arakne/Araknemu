package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.util.Base64;

import java.util.HashMap;
import java.util.Map;

/**
 * Transformer for map cell
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/ank/battlefield/utils/Compressor.as#L54
 */
final public class MapCellTransformer implements Transformer<MapTemplate.Cell> {
    private static class ByteArrayCell implements MapTemplate.Cell {
        final private byte[] data;

        public ByteArrayCell(byte[] data) {
            this.data = data;
        }

        @Override
        public boolean lineOfSight() {
            return (data[0] & 1) == 1;
        }

        @Override
        public int movement() {
            return (data[2] & 56) >> 3;
        }

        @Override
        public boolean interactive() {
            return (data[7] & 2) >> 1 == 1;
        }

        @Override
        public int objectId() {
            return ((data[0] & 2) << 12) + ((data[7] & 1) << 12) + (data[8] << 6) + data[9];
        }

        @Override
        public boolean active() {
            return (data[0] & 32) >> 5 == 1;
        }
    }

    final private Map<String, ByteArrayCell> cache = new HashMap<>();

    @Override
    public String serialize(MapTemplate.Cell value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MapTemplate.Cell unserialize(String serialize) {
        if (cache.containsKey(serialize)) {
            return cache.get(serialize);
        }

        byte[] data = new byte[serialize.length()];

        for (int i = 0; i < serialize.length(); ++i) {
            data[i] = (byte) Base64.ord(serialize.charAt(i));
        }

        ByteArrayCell cell =  new ByteArrayCell(data);

        cache.put(serialize, cell);

        return cell;
    }
}
