package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle fight effects
 */
final public class EffectsHandler {
    final private Map<Integer, EffectHandler> handlers = new HashMap<>();

    public void register(int effectId, EffectHandler applier) {
        handlers.put(effectId, applier);
    }

    /**
     * Apply a cast to the fight
     */
    public void apply(CastScope cast) {
        for (Fighter target : cast.targets()) {
            target.buffs().onCastTarget(cast);
        }

        for (CastScope.EffectScope effect : cast.effects()) {
            // @todo Warning if handler is not found
            if (handlers.containsKey(effect.effect().effect())) {
                EffectHandler handler = handlers.get(effect.effect().effect());

                if (effect.effect().duration() == 0) {
                    handler.handle(cast, effect);
                } else {
                    handler.buff(cast, effect);
                }
            }
        }
    }
}
