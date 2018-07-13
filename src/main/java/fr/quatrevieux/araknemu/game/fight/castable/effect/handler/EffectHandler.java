package fr.quatrevieux.araknemu.game.fight.castable.effect.handler;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Handle a fight effect
 */
public interface EffectHandler {
    /**
     * Handle the effect on the target
     */
    public void handle(Fighter caster, Castable castable, SpellEffect effect, FightCell target);

    /**
     * Apply a buff to the target
     *
     * @param caster The caster
     * @param castable The cast action
     * @param effect The effect to apply
     * @param target The target cell
     */
    public void buff(Fighter caster, Castable castable, SpellEffect effect, FightCell target);
}
