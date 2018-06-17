package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.util.Base64;

/**
 * Transform spell / weapon effect area string
 */
final public class EffectAreaTransformer implements Transformer<EffectArea> {
    @Override
    public String serialize(EffectArea value) {
        if (value == null) {
            return null;
        }

        return new String(new char[] {value.type().c(), Base64.chr(value.size())});
    }

    @Override
    public EffectArea unserialize(String serialize) {
        if (serialize == null) {
            return null;
        }

        return new EffectArea(
            EffectArea.Type.byChar(serialize.charAt(0)),
            Base64.ord(serialize.charAt(1))
        );
    }
}
