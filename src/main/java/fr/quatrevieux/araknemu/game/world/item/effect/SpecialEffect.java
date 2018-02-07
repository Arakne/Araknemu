package fr.quatrevieux.araknemu.game.world.item.effect;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.util.Arrays;

/**
 * Special effect
 */
final public class SpecialEffect implements ItemEffect {
    final private Effect effect;
    final private int[] arguments;
    final private String text;

    public SpecialEffect(Effect effect, int[] arguments, String text) {
        this.effect = effect;
        this.arguments = arguments;
        this.text = text;
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
            text
        );
    }

    public int[] arguments() {
        return arguments;
    }

    public String text() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        SpecialEffect that = (SpecialEffect) o;

        return effect == that.effect
            && Arrays.equals(arguments, that.arguments)
            && text.equals(that.text)
        ;
    }

    @Override
    public int hashCode() {
        int result = effect.hashCode();

        result = 31 * result + Arrays.hashCode(arguments);
        result = 31 * result + text.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "SpecialEffect{" + effect + ":" + Arrays.toString(arguments) + ", '" + text + "'}";
    }
}
