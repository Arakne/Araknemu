package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.TargetResolver;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Skip the fighter turn until buff is expired
 */
final public class SkipTurnHandler implements EffectHandler, BuffHook {
    final private Fight fight;

    public SkipTurnHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(Fighter caster, Castable castable, SpellEffect effect, FightCell target) {
        buff(caster, castable, effect, target);
    }

    @Override
    public void buff(Fighter caster, Castable castable, SpellEffect effect, FightCell target) {
        new TargetResolver(caster, target)
            .area(effect.area())
            .fighters(fighter -> fighter.buffs().add(new Buff(effect, castable, caster, fighter, this)))
        ;
    }

    @Override
    public boolean onStartTurn(Buff buff) {
        return false;
    }

    @Override
    public void onBuffStarted(Buff buff) {
        fight.send(ActionEffect.skipNextTurn(buff.caster(), buff.target()));
    }
}
