package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Handle simple damage effect
 */
final public class DamageHandler implements EffectHandler, BuffHook {
    final private Element element;

    public DamageHandler(Element element) {
        this.element = element;
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        DamageApplier applier = new DamageApplier(cast.caster(), effect.effect(), element);

        for (Fighter target : effect.targets()) {
            applier.apply(target);
        }
    }

    @Override
    public void buff(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public boolean onStartTurn(Buff buff) {
        new DamageApplier(buff.caster(), buff.effect(), element).accept(buff.target());

        return !buff.target().dead();
    }
}
