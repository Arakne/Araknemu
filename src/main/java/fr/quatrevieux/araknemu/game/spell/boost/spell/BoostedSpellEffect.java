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
        return applyBoost(effect.min());
    }

    @Override
    public int max() {
        if (effect.max() == 0) {
            return 0;
        }

        return applyBoost(effect.max());
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

    private int applyBoost(int base) {
        if (isBoostableDamageEffect()) {
            return base + modifiers.damage();
        }

        if (isBoostableHealEffect()) {
            return base + modifiers.heal();
        }

        return base;
    }

    private boolean isBoostableDamageEffect() {
        return effect() >= 93 && effect() <= 100;
    }

    private boolean isBoostableHealEffect() {
        return
            effect() == 81
            || effect() == 108
        ;
    }
}
