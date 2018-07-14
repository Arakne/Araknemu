package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Handle steal life
 */
final public class StealLifeHandler implements EffectHandler, BuffHook {
    final private DamageApplier applier;

    public StealLifeHandler(Element element) {
        this.applier = new DamageApplier(element);
    }

    @Override
    public void handle(CastScope cast, CastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            applyCasterHeal(applier.apply(cast.caster(), effect.effect(), target), cast.caster());
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
        applyCasterHeal(applier.apply(buff), buff.caster());

        return !buff.target().dead();
    }

    private void applyCasterHeal(int damage, Fighter caster) {
        // #56 : do not heal if dead
        if (damage >= 0 || caster.dead()) { // Heal or no effect
            return;
        }

        // Minimal heal is 1
        caster.life().alter(caster, Math.max(-damage / 2, 1));
    }
}
