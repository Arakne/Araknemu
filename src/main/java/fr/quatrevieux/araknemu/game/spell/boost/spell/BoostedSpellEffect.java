package fr.quatrevieux.araknemu.game.spell.boost.spell;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.spell.boost.SpellModifiers;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Apply spell modifiers on effect
 */
final public class BoostedSpellEffect implements SpellEffect {
    final private SpellEffect effect;
    final private SpellModifiers modifiers;

    public BoostedSpellEffect(SpellEffect effect, SpellModifiers modifiers) {
        this.effect = effect;
        this.modifiers = modifiers;
    }

    @Override
    public int effect() {
        return effect.effect();
    }

    @Override
    public int min() {
        return effect.min();
    }

    @Override
    public int max() {
        return effect.max();
    }

    @Override
    public int boost() {
        if (isBoostableDamageEffect()) {
            return modifiers.damage();
        }

        if (isBoostableHealEffect()) {
            return modifiers.heal();
        }

        return 0;
    }

    @Override
    public int special() {
        return effect.special();
    }

    @Override
    public int duration() {
        return effect.duration();
    }

    @Override
    public int probability() {
        return effect.probability();
    }

    @Override
    public String text() {
        return effect.text();
    }

    @Override
    public EffectArea area() {
        return effect.area();
    }

    @Override
    public int target() {
        return effect.target();
    }

    private boolean isBoostableDamageEffect() {
        return effect() >= 91 && effect() <= 100;
    }

    private boolean isBoostableHealEffect() {
        return
            effect() == 81
            || effect() == 108
        ;
    }
}
