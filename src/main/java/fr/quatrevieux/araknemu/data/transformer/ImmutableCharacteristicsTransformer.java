package fr.quatrevieux.araknemu.data.transformer;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Adapter for transform immutable characteristics
 */
final public class ImmutableCharacteristicsTransformer implements Transformer<Characteristics> {
    final private CharacteristicsTransformer inner = new CharacteristicsTransformer();

    @Override
    public String serialize(Characteristics value) {
        return inner.serialize(value);
    }

    @Override
    public Characteristics unserialize(String serialize) {
        return inner.unserialize(serialize);
    }
}
