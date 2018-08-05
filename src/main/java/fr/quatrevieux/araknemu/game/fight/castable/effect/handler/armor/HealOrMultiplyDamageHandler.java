package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Random;

/**
 * Suffered damage will be healed, or multiplied
 */
final public class HealOrMultiplyDamageHandler implements EffectHandler, BuffHook {
    final private Random random = new Random();

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("HealOrMultiplyDamageHandler can only be used as buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDamage(Buff buff, Damage value) {
        boolean heal = random.nextInt(100) < buff.effect().special();

        if (heal) {
            value.multiply(-buff.effect().max());
        } else {
            value.multiply(buff.effect().min());
        }
    }
}
