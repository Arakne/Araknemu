package fr.quatrevieux.araknemu.game.spell.effect;

import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;

/**
 * Type effect for spells
 */
public interface SpellEffect {
    /**
     * Get the effect
     */
    public int effect();

    /**
     * Get the minimal jet value, or the first argument
     */
    public int min();

    /**
     * Get the maximal jet value, or the first argument
     *
     * If the value is zero, the effect value is constant (min)
     */
    public int max();

    /**
     * Get the boost value
     * The boost value will be added to final effect result (fixed damage / heal)
     */
    default public int boost() {
        return 0;
    }

    /**
     * Get the special effect value
     * Used by invocation and boost spells
     */
    public int special();

    /**
     * Get the effect duration
     * If this value is zero, the effect will be applied immediately
     */
    public int duration();

    /**
     * The effect probability in percent
     * If this value is zero, the effect will always be applied
     * For not null probability, only one effect will be choose across all "conditional" effects
     */
    public int probability();

    /**
     * Extra effect text. Used for dice notation
     */
    public String text();

    /**
     * Get the effect area
     */
    public SpellEffectArea area();

    /**
     * Get the effect target
     */
    public int target();
}
