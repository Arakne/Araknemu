package fr.quatrevieux.araknemu.game.item.effect;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.use.UseEffectHandler;

import java.util.Arrays;

/**
 * Effect for usable items
 */
final public class UseEffect implements ItemEffect {
    final private UseEffectHandler handler;
    final private Effect effect;
    final private int[] arguments;

    public UseEffect(UseEffectHandler handler, Effect effect, int[] arguments) {
        this.handler = handler;
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

    /**
     * Check if the player can use the effect
     */
    public boolean check(ExplorationPlayer caster) {
        return handler.check(this, caster);
    }

    /**
     * Check if the player can use the effect to the target
     */
    public boolean checkTarget(ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return handler.checkTarget(this, caster, target, cell);
    }

    /**
     * Apply the effect to the player
     */
    public void apply(ExplorationPlayer caster) {
        handler.apply(this, caster);
    }

    /**
     * Apply the effect to the target
     */
    public void applyToTarget(ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        handler.applyToTarget(this, caster, target, cell);
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
