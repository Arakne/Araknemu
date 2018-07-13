package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.TargetResolver;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Handle simple damage effect
 */
final public class DamageHandler implements EffectHandler, BuffHook {
    final private Element element;

    public DamageHandler(Element element) {
        this.element = element;
    }

    @Override
    public void handle(Fighter caster, Castable castable, SpellEffect effect, FightCell target) {
        new TargetResolver(caster, target)
            .area(effect.area())
            .fighters(new DamageApplier(caster, effect, element))
        ;
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
        new DamageApplier(buff.caster(), buff.effect(), element).accept(buff.target());

        return !buff.target().dead();
    }
}
