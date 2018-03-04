package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.AddSpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.NullEffectHandler;
import fr.quatrevieux.araknemu.game.item.effect.special.SpecialEffectHandler;
import fr.quatrevieux.araknemu.game.item.effect.special.SubSpecialEffect;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Map item effect to special effect
 */
final public class EffectToSpecialMapping implements EffectMapper<SpecialEffect> {
    final private Map<Effect, SpecialEffectHandler> handlers = new EnumMap<>(Effect.class);

    public EffectToSpecialMapping() {
        handlers.put(Effect.ADD_DISCERNMENT, new AddSpecialEffect(SpecialEffects.Type.DISCERNMENT));
        handlers.put(Effect.ADD_PODS,        new AddSpecialEffect(SpecialEffects.Type.PODS));
        handlers.put(Effect.ADD_INITIATIVE,  new AddSpecialEffect(SpecialEffects.Type.INITIATIVE));

        handlers.put(Effect.SUB_DISCERNMENT, new SubSpecialEffect(SpecialEffects.Type.DISCERNMENT));
        handlers.put(Effect.SUB_PODS,        new SubSpecialEffect(SpecialEffects.Type.PODS));
        handlers.put(Effect.SUB_INITIATIVE,  new SubSpecialEffect(SpecialEffects.Type.INITIATIVE));
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        return handlers.getOrDefault(entry.effect(), NullEffectHandler.INSTANCE).create(entry, maximize);
    }

    @Override
    public List<SpecialEffect> create(List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return effects
            .stream()
            .filter(effect -> effect.effect().type() == Effect.Type.SPECIAL)
            .map(effect -> create(effect, maximize))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public Class<SpecialEffect> type() {
        return SpecialEffect.class;
    }
}
