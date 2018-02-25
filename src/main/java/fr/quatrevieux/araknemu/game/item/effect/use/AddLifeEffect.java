package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Add life to the use target
 */
final public class AddLifeEffect implements UseEffectHandler {
    final private RandomUtil random = new RandomUtil();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        int value = random.rand(effect.arguments());

        caster.life().add(value);
    }

    @Override
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        apply(effect, target);
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return !caster.life().isFull();
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return target != null && check(effect, target);
    }
}
