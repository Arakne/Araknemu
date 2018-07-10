package fr.quatrevieux.araknemu.game.fight.castable.effect.handler;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Handle a fight effect
 */
public interface EffectHandler {
    /**
     * Handle the effect on the target
     */
    public void handle(Fighter caster, Castable castable, SpellEffect effect, FightCell target);
}
