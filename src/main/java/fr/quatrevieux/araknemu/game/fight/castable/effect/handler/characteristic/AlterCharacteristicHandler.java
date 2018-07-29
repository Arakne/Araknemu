package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Alter a characteristic with buff effect
 */
abstract public class AlterCharacteristicHandler implements EffectHandler, BuffHook {
    final private Fight fight;
    final private Characteristic characteristic;

    public AlterCharacteristicHandler(Fight fight, Characteristic characteristic) {
        this.fight = fight;
        this.characteristic = characteristic;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        throw new UnsupportedOperationException("Alter characteristic effect must be used as a buff");
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        SpellEffect buffEffect = computeBuffEffect(cast, effect.effect());

        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(buffEffect, cast.action(), cast.caster(), target, this));
        }
    }

    /**
     * Compute the buff value

     * @fixme One dice for all targets, or one dice per target ?
     */
    private SpellEffect computeBuffEffect(CastScope cast, SpellEffect effect) {
        EffectValue value = new EffectValue(effect);

        return new BuffEffect(effect, value.value());
    }

    @Override
    public void onBuffStarted(Buff buff) {
        buff.target().characteristics().alter(characteristic, value(buff));
        fight.send(ActionEffect.buff(buff, value(buff)));
    }

    @Override
    public void onBuffTerminated(Buff buff) {
        buff.target().characteristics().alter(characteristic, -value(buff));
    }

    /**
     * Get the buff effect value
     */
    abstract protected int value(Buff buff);
}
