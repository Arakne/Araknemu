package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.TargetResolver;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Handle steal life
 */
final public class StealLifeHandler implements EffectHandler {
    final private Element element;

    public StealLifeHandler(Element element) {
        this.element = element;
    }

    @Override
    public void handle(Fighter caster, Spell spell, SpellEffect effect, FightCell target) {
        new TargetResolver(caster, target)
            .area(effect.area())
            .map(new DamageApplier(caster, effect, element))
            .forEach(damage -> {
                // #56 : do not heal if dead
                if (damage >= 0 || caster.dead()) { // Heal or no effect
                    return;
                }

                // Minimal heal is 1
                caster.life().alter(caster, Math.max(-damage / 2, 1));
            })
        ;
    }
}
