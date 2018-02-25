package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;

/**
 * Effect handler for an use effect
 */
public interface UseEffectHandler {
    /**
     * Apply the item effect to the caster
     *
     * @param effect The effect to apply
     * @param caster The caster
     */
    public void apply(UseEffect effect, ExplorationPlayer caster);

    /**
     * Apply the item effect to a target player or cell
     *
     * @param effect The effect to apply
     * @param caster The effect caster
     * @param target The effect target (can be null)
     * @param cell The target cell
     */
    public void applyToTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell);

    /**
     * Check if the effect can be used
     *
     * @param effect The effect to apply
     * @param caster The caster
     *
     * @return True if the effect can be applied or false
     */
    public boolean check(UseEffect effect, ExplorationPlayer caster);

    /**
     * Check if the effect can be used to the target
     *
     * @param effect The effect to apply
     * @param caster The caster
     * @param target The effect target (can be null)
     * @param cell The target cell
     *
     * @return True if the effect can be applied or false
     */
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, ExplorationPlayer target, int cell);
}
