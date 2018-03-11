package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;

/**
 * Adapt {@link SpellTemplateEffect}
 */
final public class SpellTemplateEffectAdapter implements SpellEffect {
    final private SpellTemplateEffect effect;
    final private EffectArea area;
    final private int target;

    public SpellTemplateEffectAdapter(SpellTemplateEffect effect, EffectArea area, int target) {
        this.effect = effect;
        this.area = area;
        this.target = target;
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
        return area;
    }

    @Override
    public int target() {
        return target;
    }
}
