package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Handle push state to the fighter
 */
final public class PushStateHandler implements EffectHandler {
    final private Fight fight;

    public PushStateHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.states().push(effect.effect().special());
        }
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            int duration = effect.effect().duration();

            if (target.equals(fight.turnList().currentFighter())) {
                ++duration;
            }

            target.states().push(effect.effect().special(), duration);
        }
    }
}
