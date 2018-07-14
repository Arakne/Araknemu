package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Apply simple damage to fighter
 *
 * Returns the effect damage value.
 * When applies damage, a negative value will be returned (-50 => The target lose 50 LP)
 * When no effect, zero will be returned
 * When damage is transformed to heal, will return a positive value (50 => The target win 50 LP)
 */
final public class DamageApplier {
    final private Element element;

    public DamageApplier(Element element) {
        this.element = element;
    }

    /**
     * Apply a damage effect to a fighter
     *
     * @param caster The spell caster
     * @param effect The effect to apply
     * @param target The target
     *
     * @return The real damage value
     */
    public int apply(Fighter caster, SpellEffect effect, Fighter target) {
        EffectValue value = new EffectValue(effect)
            .percent(caster.characteristics().get(element.boost()))
            .percent(caster.characteristics().get(Characteristic.PERCENT_DAMAGE))
            .fixed(caster.characteristics().get(Characteristic.FIXED_DAMAGE))
        ;

        Damage damage = new Damage(value.value())
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;

        // @todo returned damage

        return target.life().alter(caster, -damage.value());
    }

    /**
     * Apply a damage buff effect
     *
     * @param buff Buff to apply
     *
     * @return The realm damage value
     */
    public int apply(Buff buff) {
        return apply(buff.caster(), buff.effect(), buff.target());
    }
}
