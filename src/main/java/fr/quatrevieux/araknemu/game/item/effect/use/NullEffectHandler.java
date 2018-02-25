package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

/**
 * Null object for {@link UseEffectHandler}
 */
final public class NullEffectHandler implements UseEffectHandler {
    final static public UseEffectHandler INSTANCE = new NullEffectHandler();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {}

    @Override
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {}

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return true;
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return true;
    }
}
