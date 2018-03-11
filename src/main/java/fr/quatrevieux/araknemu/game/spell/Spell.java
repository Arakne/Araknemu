package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;

/**
 * Interface for spells
 */
public interface Spell {
    /**
     * Get the spell id
     */
    public int id();

    /**
     * Get the spell level in interval [1-6]
     */
    public int level();

    /**
     * List of normal spell effects
     */
    public List<SpellEffect> effects();

    /**
     * List of critical spell effects
     */
    public List<SpellEffect> criticalEffects();

    /**
     * The AP cost
     */
    public int apCost();

    /**
     * Percent of chance for get critical hit
     */
    public int criticalHit();

    /**
     * Percent of chance for get a critical failure
     */
    public int criticalFailure();

    /**
     * Does the spell range is modifiable ?
     */
    public boolean modifiableRange();

    /**
     * Minimal player level for use the spell
     */
    public int minPlayerLevel();

    /**
     * Does critical failures will ends the current fight turn ?
     */
    public boolean endsTurnOnFailure();

    /**
     * Get the launch constraints
     */
    public SpellConstraints constraints();
}
