package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.TargetResolver;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Handle simple damage effect
 */
final public class DamageHandler implements EffectHandler {
    final private Element element;

    public DamageHandler(Element element) {
        this.element = element;
    }

    @Override
    public void handle(Fighter caster, Spell spell, SpellEffect effect, FightCell target) {
        new TargetResolver(caster, target)
            .area(effect.area())
            .fighters(new DamageApplier(caster, effect, element))
        ;
    }
}
