package fr.quatrevieux.araknemu.game.fight.castable.effect.handler;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;

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
