package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Map item effect to weapon effect
 */
final public class EffectToWeaponMapping implements EffectMapper<WeaponEffect> {
    /**
     * Create the effect from the template
     */
    public WeaponEffect create(ItemTemplateEffectEntry entry) {
        return new WeaponEffect(entry.effect(), entry.min(), entry.max(), entry.special());
    }

    @Override
    public WeaponEffect create(ItemTemplateEffectEntry effect, boolean maximize) {
        return create(effect);
    }

    @Override
    public List<WeaponEffect> create(List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return create(effects);
    }

    @Override
    public List<WeaponEffect> create(List<ItemTemplateEffectEntry> effects) {
        return effects
            .stream()
            .filter(e -> e.effect().type() == Effect.Type.WEAPON)
            .map(this::create)
            .collect(Collectors.toList())
        ;
    }

    @Override
    public Class<WeaponEffect> type() {
        return WeaponEffect.class;
    }
}
