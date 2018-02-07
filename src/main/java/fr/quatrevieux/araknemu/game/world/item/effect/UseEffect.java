package fr.quatrevieux.araknemu.game.world.item.effect;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.util.Arrays;

/**
 * Effect for usable items
 */
final public class UseEffect implements ItemEffect {
    final private Effect effect;
    final private int[] arguments;

    public UseEffect(Effect effect, int[] arguments) {
        this.effect = effect;
        this.arguments = arguments;
    }

    @Override
    public Effect effect() {
        return effect;
    }

    @Override
    public ItemTemplateEffectEntry toTemplate() {
        return new ItemTemplateEffectEntry(
            effect,
            arguments[0],
            arguments[1],
            arguments[2],
            ""
        );
    }

    public int[] arguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        UseEffect useEffect = (UseEffect) o;

        return effect == useEffect.effect
            && Arrays.equals(arguments, useEffect.arguments)
        ;
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + Arrays.hashCode(arguments);

        return result;
    }

    @Override
    public String toString() {
        return "UseEffect{" + effect + ":" + Arrays.toString(arguments) + '}';
    }
}
