package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Handle reducing damages
 */
final public class ReduceDamageHandler implements EffectHandler, BuffHook {
    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("ReduceDamageHandler can only be used as buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onDamage(Buff buff, Damage value) {
        if (!supportsElement(buff, value.element())) {
            return;
        }

        final Characteristics characteristics = buff.target().characteristics();

        int boost = 200 + characteristics.get(Characteristic.INTELLIGENCE) + characteristics.get(value.element().boost());
        int reduce = buff.effect().min() * boost / 200;

        if (reduce < 0) {
            return;
        }

        value.reduce(reduce);
    }

    /**
     * Check if the armor supports the damage element
     *
     * @param buff The buff
     * @param element The damage element
     *
     * @return true if the armor supports the element
     */
    private boolean supportsElement(Buff buff, Element element) {
        return buff.effect().special() == 0 || Element.fromBitSet(buff.effect().special()).contains(element);
    }
}
