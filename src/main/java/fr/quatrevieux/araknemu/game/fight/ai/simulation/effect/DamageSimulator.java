package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Simulate simple damage effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageHandler
 */
final public class DamageSimulator implements EffectSimulator {
    final private Element element;

    public DamageSimulator(Element element) {
        this.element = element;
    }

    @Override
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect) {
        int value = new EffectValue(effect.effect())
            .percent(simulation.caster().characteristics().get(element.boost()))
            .percent(simulation.caster().characteristics().get(Characteristic.PERCENT_DAMAGE))
            .fixed(simulation.caster().characteristics().get(Characteristic.FIXED_DAMAGE))
            .mean()
            .value()
        ;

        for (Fighter target : effect.targets()) {
            Damage damage = new Damage(value, element)
                .percent(target.characteristics().get(element.percentResistance()))
                .fixed(target.characteristics().get(element.fixedResistance()))
            ;

            simulation.addDamage(Math.max(applyBuff(damage.value(), effect.effect()), 1), target);
        }
    }

    /**
     * Modify the damage value by applying effect duration (poison)
     *
     * @param baseValue The base damage value
     * @param effect The spell effect
     */
    private int applyBuff(int baseValue, SpellEffect effect) {
        if (effect.duration() < 1) {
            return baseValue;
        }

        return (int) (baseValue * effect.duration() * 0.75);
    }
}
