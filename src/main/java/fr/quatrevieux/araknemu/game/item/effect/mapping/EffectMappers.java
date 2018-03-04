package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for effect mappers
 */
final public class EffectMappers {
    final private Map<Class<? extends ItemEffect>, EffectMapper> mappers = new HashMap<>();

    public EffectMappers(EffectMapper... mappers) {
        for (EffectMapper mapper : mappers) {
            register(mapper);
        }
    }

    /**
     * Get mapper for the requested item effect type
     *
     * @param type The requested item type
     */
    public <E extends ItemEffect> EffectMapper<E> get(Class<E> type) {
        return mappers.get(type);
    }

    private void register(EffectMapper mapper) {
        mappers.put(mapper.type(), mapper);
    }
}
