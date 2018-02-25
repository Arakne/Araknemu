package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * Map template effect to use effect
 */
final public class EffectToUseMapping {
    final private Map<Effect, UseEffectHandler> handlers = new EnumMap<>(Effect.class);

    public EffectToUseMapping() {
        handlers.put(Effect.ADD_CHARACT_WISDOM,       new AddCharacteristicEffect(Characteristic.WISDOM));
        handlers.put(Effect.ADD_CHARACT_STRENGTH,     new AddCharacteristicEffect(Characteristic.STRENGTH));
        handlers.put(Effect.ADD_CHARACT_LUCK,         new AddCharacteristicEffect(Characteristic.LUCK));
        handlers.put(Effect.ADD_CHARACT_AGILITY,      new AddCharacteristicEffect(Characteristic.AGILITY));
        handlers.put(Effect.ADD_CHARACT_VITALITY,     new AddCharacteristicEffect(Characteristic.VITALITY));
        handlers.put(Effect.ADD_CHARACT_INTELLIGENCE, new AddCharacteristicEffect(Characteristic.INTELLIGENCE));

        handlers.put(Effect.ADD_LIFE, new AddLifeEffect());

        handlers.put(Effect.FIREWORK, new FireworkEffect());
    }

    /**
     * Create the use effect
     *
     * @param entry The effect template
     */
    public UseEffect create(ItemTemplateEffectEntry entry) {
        return new UseEffect(
            handlers.getOrDefault(entry.effect(), NullEffectHandler.INSTANCE),
            entry.effect(),
            new int[] {entry.min(), entry.max(), entry.special()}
        );
    }
}
