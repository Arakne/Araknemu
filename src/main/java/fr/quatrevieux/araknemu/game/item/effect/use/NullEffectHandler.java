package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

/**
 * Null object for {@link UseEffectHandler}
 */
final public class NullEffectHandler implements UseEffectHandler {
    final static public UseEffectHandler INSTANCE = new NullEffectHandler();

    @Override
    public void apply(UseEffect effect, ExplorationPlayer caster) {
        // No-op method
    }

    @Override
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell) {
        return true;
    }

    @Override
    public boolean check(UseEffect effect, ExplorationPlayer caster) {
        return true;
    }

    @Override
    public boolean checkFighter(UseEffect effect, PlayerFighter fighter) {
        return true;
    }
}
