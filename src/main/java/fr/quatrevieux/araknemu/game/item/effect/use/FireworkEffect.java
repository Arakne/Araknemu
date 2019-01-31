package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.environment.LaunchFirework;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Effect for fireworks
 */
final public class FireworkEffect implements UseEffectHandler {
    final private RandomUtil random = new RandomUtil();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        throw new UnsupportedOperationException("Cannot apply firework on self");
    }

    @Override
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        int size = random.rand(effect.arguments());

        try {
            caster.interactions().push(
                new LaunchFirework(
                    caster,
                    cell,
                    effect.arguments()[2],
                    size
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return false;
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return true; // @todo check if cell exists
    }
}
