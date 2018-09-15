package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Handle remove state from the fighter
 */
final public class RemoveStateHandler implements EffectHandler {
    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.states().remove(effect.effect().special());
        }
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Cannot remove state from a buff");
    }
}
