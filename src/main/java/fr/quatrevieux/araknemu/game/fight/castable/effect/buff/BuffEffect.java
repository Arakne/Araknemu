package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;

/**
 * Overrides effect parameters for buff effect
 */
final public class BuffEffect implements SpellEffect {
    final private SpellEffect baseEffect;

    final private int min;
    final private int max;
    final private int special;
    final private String text;

    public BuffEffect(SpellEffect baseEffect, int min) {
        this(baseEffect, min, 0, 0);
    }

    public BuffEffect(SpellEffect baseEffect, int min, int max) {
        this(baseEffect, min, max, 0);
    }

    public BuffEffect(SpellEffect baseEffect, int min, int max, int special) {
        this(baseEffect, min, max, special, null);
    }

    public BuffEffect(SpellEffect baseEffect, int min, int max, int special, String text) {
        this.baseEffect = baseEffect;
        this.min = min;
        this.max = max;
        this.special = special;
        this.text = text;
    }

    @Override
    public int effect() {
        return baseEffect.effect();
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public int special() {
        return special;
    }

    @Override
    public int duration() {
        return baseEffect.duration();
    }

    @Override
    public int probability() {
        return 0;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public SpellEffectArea area() {
        return baseEffect.area();
    }

    @Override
    public EffectTarget target() {
        return baseEffect.target();
    }
}
