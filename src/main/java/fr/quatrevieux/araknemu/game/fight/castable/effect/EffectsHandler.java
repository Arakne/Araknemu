package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.SkipTurnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.TeleportHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle fight effects
 */
final public class EffectsHandler {
    final private Fight fight;

    final private Map<Integer, EffectHandler> handlers = new HashMap<>();

    public EffectsHandler(Fight fight) {
        this.fight = fight;

        register(4, new TeleportHandler(fight));

        register(91, new StealLifeHandler(Element.WATER));
        register(92, new StealLifeHandler(Element.EARTH));
        register(93, new StealLifeHandler(Element.AIR));
        register(94, new StealLifeHandler(Element.FIRE));
        register(95, new StealLifeHandler(Element.NEUTRAL));

        register(96,  new DamageHandler(Element.WATER));
        register(97,  new DamageHandler(Element.EARTH));
        register(98,  new DamageHandler(Element.AIR));
        register(99,  new DamageHandler(Element.FIRE));
        register(100, new DamageHandler(Element.NEUTRAL));

        register(140, new SkipTurnHandler(fight));
    }

    public void register(int effectId, EffectHandler applier) {
        handlers.put(effectId, applier);
    }

    /**
     * Apply a cast to the fight
     */
    public void apply(CastScope cast) {
        for (CastScope.EffectScope effect : cast.effects()) {
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
