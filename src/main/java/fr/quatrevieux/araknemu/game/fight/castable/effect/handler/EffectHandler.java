package fr.quatrevieux.araknemu.game.fight.castable.effect.handler;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.Collection;

/**
 * Handle a fight effect
 */
public interface EffectHandler {
    /**
     * Handle the effect on the target
     */
    public void handle(CastScope cast, CastScope.EffectScope effect);

    /**
     * Apply a buff to the target
     *
     * @param cast The cast action arguments
     * @param effect The effect to apply
     */
    public void buff(CastScope cast, CastScope.EffectScope effect);
}
