package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.HealOrMultiplyDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.ReduceDamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor.SpellReturnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.*;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.StealLifeHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.PushStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.RemoveStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.SkipTurnHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.TeleportHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

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

        register(91, new StealLifeHandler(Element.WATER, fight));
        register(92, new StealLifeHandler(Element.EARTH, fight));
        register(93, new StealLifeHandler(Element.AIR, fight));
        register(94, new StealLifeHandler(Element.FIRE, fight));
        register(95, new StealLifeHandler(Element.NEUTRAL, fight));

        register(96,  new DamageHandler(Element.WATER, fight));
        register(97,  new DamageHandler(Element.EARTH, fight));
        register(98,  new DamageHandler(Element.AIR, fight));
        register(99,  new DamageHandler(Element.FIRE, fight));
        register(100, new DamageHandler(Element.NEUTRAL, fight));

        register(140, new SkipTurnHandler(fight));
        register(950, new PushStateHandler(fight));
        register(951, new RemoveStateHandler());

        register(79,  new HealOrMultiplyDamageHandler());
        register(105, new ReduceDamageHandler());
        register(106, new SpellReturnHandler(fight));
        register(265, new ReduceDamageHandler());

        register(111, new AddActionPointsHandler(fight));
        register(120, new AddActionPointsHandler(fight));
        register(168, new RemoveActionPointsHandler(fight));

        register(78,  new AddMovementPointsHandler(fight));
        register(128, new AddMovementPointsHandler(fight));
        register(169, new RemoveMovementPointsHandler(fight));

        register(112, new AddCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE));
        register(115, new AddCharacteristicHandler(fight, Characteristic.CRITICAL_BONUS));
        register(117, new AddCharacteristicHandler(fight, Characteristic.SIGHT_BOOST));
        register(118, new AddCharacteristicHandler(fight, Characteristic.STRENGTH));
        register(119, new AddCharacteristicHandler(fight, Characteristic.AGILITY));
        register(122, new AddCharacteristicHandler(fight, Characteristic.FAIL_MALUS));
        register(123, new AddCharacteristicHandler(fight, Characteristic.LUCK));
        register(124, new AddCharacteristicHandler(fight, Characteristic.WISDOM));
        register(126, new AddCharacteristicHandler(fight, Characteristic.INTELLIGENCE));
        register(138, new AddCharacteristicHandler(fight, Characteristic.PERCENT_DAMAGE));
        register(178, new AddCharacteristicHandler(fight, Characteristic.HEALTH_BOOST));
        register(182, new AddCharacteristicHandler(fight, Characteristic.MAX_SUMMONED_CREATURES));

        register(116, new RemoveCharacteristicHandler(fight, Characteristic.SIGHT_BOOST));
        register(145, new RemoveCharacteristicHandler(fight, Characteristic.FIXED_DAMAGE));
        register(152, new RemoveCharacteristicHandler(fight, Characteristic.LUCK));
        register(154, new RemoveCharacteristicHandler(fight, Characteristic.AGILITY));
        register(155, new RemoveCharacteristicHandler(fight, Characteristic.INTELLIGENCE));
        register(156, new RemoveCharacteristicHandler(fight, Characteristic.WISDOM));
        register(157, new RemoveCharacteristicHandler(fight, Characteristic.STRENGTH));
        register(171, new RemoveCharacteristicHandler(fight, Characteristic.CRITICAL_BONUS));
        register(179, new RemoveCharacteristicHandler(fight, Characteristic.HEALTH_BOOST));
        register(186, new RemoveCharacteristicHandler(fight, Characteristic.PERCENT_DAMAGE));
    }

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
